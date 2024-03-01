package logic;

import logic.Logging.LogMessage;
import logic.Tile.TileRegistry;

import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The `Game` class represents the core logic of a game.<br>It manages the game state, players, turns, and actions.<br>
 * This class handles the initialization of the game, the game loop, and various game-related functions.<br>
 * It implements the {@link GameContext} interface to provide context-specific information to game components.<br>
 * <br>
 * Details on how the game and it's rules work can be found in the Manual.
 *
 * @author Lukas Frahm
 * @version 1.0
 */
public class Game implements GameContext {

    // ==========================================================
    // Constants
    // ==========================================================

    /**
     * The base amount of carrots that are exchanged in every {@link Action} involving a carrot exchange.
     */
    public static final int CARROT_EXCHANGE_AMOUNT = 10;

    /**
     * The amount of carrots that every player starts with in a game with
     * fewer than {@link #MIN_PLAYERS_FOR_VARIANT} players.
     * @see #STARTING_CARROTS_VARIANT
     */
    public static final int STARTING_CARROTS = 68;

    /**
     * The amount of carrots that every player starts with in a game,
     * after the player count reaches {@link #MIN_PLAYERS_FOR_VARIANT}.
     * @see #STARTING_CARROTS
     */
    public static final int STARTING_CARROTS_VARIANT = 98;

    /**
     * The amount of salads that every player starts with.
     */
    public static final int STARTING_SALADS = 3;

    /**
     * The minimum amount of players for the {@link #STARTING_CARROTS_VARIANT} to replace {@link #STARTING_CARROTS}.
     */
    public static final int MIN_PLAYERS_FOR_VARIANT = 5;

    /**
     * A {@link Runnable} that does nothing. Used to improve readability.
     */
    public static final Runnable NO_OP = () -> {};

    /**
     * <b>Only used for testing.</b><br>
     * Set this to a constant value to get a deterministic game.
     * Set this to null to get a random game.
     */
    private static final Long GAME_SEED = null;

    // ==========================================================
    // Instance Variables
    // ==========================================================

    /**
     * An array of players in the game, including those who have finished. Ordered by whose starting first.
     */
    private final Player[] players;

    /**
     * A stack of action cards for the game, with the top card drawn first.
     */
    private transient final ActionCardStack cards;

    /**
     * A list of players who have finished the game, ordered by whose finished first.
     */
    private transient final ArrayList<Player> finishedPlayers;

    /**
     * The seed used for generating the order of action cards.<br><br>
     * It is randomly generated in the constructor if {@link #GAME_SEED} is null.
     */
    private transient final long gameSeed;

    /**
     * The connector used for communication with the GUI.
     */
    private transient final GUIConnector gui;

    /**
     * The index of the current player in the {@link #players} array.
     */
    private int currentPlayer;


    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * Creates a new {@link Game} with the given parameters. This is the main constructor.<br>
     * <br>
     * @param players Array of {@link Player}s representing the players in the game,
     *                including those who have finished. The array should be ordered
     *                by their initial starting order.
     * @param finishedPlayers Array of indices representing the players that have finished the game.
     *                        The indices correspond to the {@code players} array and should be ordered
     *                        by the sequence in which the players finished.
     * @param gui The {@link GUIConnector} instance used for communication with the GUI.
     * @param currentPlayer The index of the current player in the {@code players} array. This indicates
     *                      whose turn it is.
     */
    Game(Player[] players, int[] finishedPlayers, GUIConnector gui, int currentPlayer) {
        this.players = players;
        this.gui = gui;
        this.gameSeed = Optional.ofNullable(GAME_SEED).orElse(new Random().nextLong());
        this.cards = new ActionCardStack(gameSeed);

        // Converts the array of finished player indices to an ArrayList of Player objects.
        // An int array is used for input for convenience, but an ArrayList is needed internally
        // for dynamic resizing and easier management of finished players.
        this.finishedPlayers = Arrays.stream(finishedPlayers)
                .mapToObj(index -> players[index])
                .collect(Collectors.toCollection(ArrayList::new));

        this.currentPlayer = currentPlayer;
    }

    /**
     * Constructs a new Game instance with specified player names and a GUI connector.
     * This constructor initializes a game with new players based on the provided names.
     * The game starts with no finished players and the first player (index 0) having the turn.
     *
     * @param playerNames Array of strings representing the names of the players.
     *                    These names are used to create new Player objects.
     * @param gui The {@link GUIConnector} instance used for communication with the GUI.
     */
    public Game(String[] playerNames, GUIConnector gui) {
        this(createPlayers(playerNames), new int[0], gui, 0);
    }

    /**
     * Constructs a new Game instance based on saved game data and a GUI connector.
     * This constructor is used for initializing a game with the state restored from
     * a previously saved game.
     * <br>
     * It doesn't use the same seed as the saved game, but instead generates a new seed
     * and doesn't know the current order of the cards stack. Therefore, the order of {@link #cards}
     * will be different from the saved game.
     *
     * @param saveGame A {@link GameData} object containing the saved state of the game,
     *                 including information about players, finished players, and the current player.
     * @param gui The {@link GUIConnector} instance used for communication with the graphical
     *            user interface.
     */
    public Game(GameData saveGame, GUIConnector gui) {
        this(saveGame.players, saveGame.finishedPlayers, gui, saveGame.currentPlayer);
    }

    /**
     * <b>ONLY USED FOR TESTS!</b>
     * @param playerNames Array of strings representing the names of the players.
     *                    These names are used to create new Player objects.
     * @param finishedPlayers Array of indices representing the players that have finished the game.
     * @param gui The {@link GUIConnector} instance used for communication with the GUI.
     */
    Game(String[] playerNames, int[] finishedPlayers, GUIConnector gui) {
        this(createPlayers(playerNames), finishedPlayers, gui, 0);
    }

    /**
     * <b>ONLY USED FOR TESTS!</b>
     * @param playerNames Array of strings representing the names of the players.
     *                    These names are used to create new Player objects.
     * @param cards The {@link ActionCardStack} to use for the game.
     *              The order of the cards will be preserved.
     * @param gui The {@link GUIConnector} instance used for communication with the GUI.
     */
    Game(String[] playerNames, ActionCard[] cards, GUIConnector gui) {
        this(playerNames, new int[0], gui);
        this.cards.matchStack(cards);
    }

    // ==========================================================
    // Methods
    // ==========================================================

    /**
     * Creates the {@link Player} objects for the game.
     * This method is specifically used when initializing a new game from scratch.
     * <br>
     * Each player is initialized with a name, a starting position on the board,
     * a specified number of carrots based on the total number of players, and
     * a default number of salads.
     *
     * @param names An array of strings representing the names of the players,
     *              sorted in the order of who starts first.
     * @return An array of {@link Player} objects, sorted in the order of who starts first.
     */
    private static Player[] createPlayers(String[] names) {
        int playerCount = names.length;

        Player[] players = new Player[playerCount];
        for (int i = 0; i < playerCount; i++) {
            players[i] = new Player(
                    names[i],
                    Board.START_FIELD,
                    getStartingCarrots(playerCount),
                    STARTING_SALADS
            );
        }

        return players;
    }

    /**
     * Starts the game loop.
     */
    public void start() {
        LogMessage.createLog(gameSeed);

        initializePlayerPositions(players.length, () -> {
            gui.selectPlayerVisual(currentPlayer);
            runGameLoop();
        });
    }

    /**
     * Starts the turn of the given {@link Player}.
     * Also handles what happens if {@link Player#isSuspended()} or {@link Player#isEatingSalad()}.
     * @param player The {@link Player} whose turn it is.
     * @param onComplete A {@link Runnable} that is called when the turn is over.
     */
    public void startTurnOfPlayer(Player player, Runnable onComplete) {
        gui.selectPlayerVisual(currentPlayer);

        gui.updatePlayerName(player.getName());
        gui.updatePlayerCarrots(player.getCarrots());
        gui.updatePlayerSalads(player.getSalads());

        LogMessage.startTurn(player.getName());

        if (player.isSuspended()) {
            gui.showIsSuspended(player.getName(), () -> {
                LogMessage.getsSkipped(player.getName());

                player.setSuspended(false);
                LogMessage.getsUnsuspended(player.getName());

                onComplete.run();
            });

        } else {
            Action action = player.isEatingSalad()
                    ? ActionBuilder.createConsumeSalad()
                    // this is what happens if the player is not suspended and not eating salad
                    // (i.e. the normal case)
                    : Board.FIELDS[player.getField()].getAction();

            action.execute(gui, this, player, onComplete);
        }
    }

    /**
     * Converts the {@link Game} to a {@link GameData} object.
     * @return The {@link GameData} object.
     */
    public GameData toGameData() {
        int[] finishedPlayers = this.finishedPlayers.stream()
                .mapToInt(player -> Arrays.asList(players).indexOf(player))
                .toArray();

        return new GameData(players, finishedPlayers, currentPlayer);
    }

    /**
     * Saves the {@link Game} to a file.
     * @param file The file to save the {@link Game} to.
     */
    public void save(File file) {
        try (Writer writer = new FileWriter(file)) {
            writer.write(toJson());
        } catch (IOException e) {
            LogMessage.error("Failed to save game to file: " + file.getPath(), e);
            gui.showSavingFailed();
        }
    }

    /**
     * Determines if the given {@link File} represents the current {@link Game}.
     * @param saveGame The {@link File} to check.
     * @return {@code true} if the given {@link File} represents the current {@link Game}, {@code false} otherwise.
     */
    public boolean representsCurrentGame(File saveGame) {
        GameData s = GameData.getGameDataFromSaveFile(saveGame);
        if (s == null) {
            return false;
        }

        if (currentPlayer != s.currentPlayer) {
            return false;
        }

        for (int i = 0; i < players.length; i++) {
            if (!players[i].equals(s.players[i])) {
                return false;
            }
        }

        if (finishedPlayers.size() != s.finishedPlayers.length) {
            return false;
        }

        for (int i = 0; i < finishedPlayers.size(); i++) {
            // not the most efficient way, but it's only used once when exiting the game
            if (!finishedPlayers.get(i).equals(s.players[s.finishedPlayers[i]])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the available fields for the given {@code player}.
     * @param player The {@link Player} whose available fields are to be returned.
     * @return The available fields for the given {@code player}, sorted ascending by field index.
     */
    @Override
    public int[] getAvailableFields(Player player) {
        // We could optimize this by only checking fields within the range of calcMaxReachablePosition.
        // However, this would require the Game class to have knowledge about the tile operations, which isn't ideal.
        // Currently, all tiles use the carrot amount as a condition, but if new types of tiles are added,
        // this could necessitate that every tile includes a carrot amount condition. Additionally,
        // the number of Board fields will never be large enough to warrant such optimization.
        return IntStream.range(0, Board.size())
                .filter(i -> Board.isAccessibleTile(i, this, player))
                .toArray();
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * @return {@link #CARROT_EXCHANGE_AMOUNT};
     */
    @Override
    public int getCarrotExchangeAmount() {
        return CARROT_EXCHANGE_AMOUNT;
    }

    /**
     * Calculates the maximum distance that can be reached with a given amount of carrots.
     * <br>
     * In the game, moving costs increase linearly with each step: <br>
     * - 1 field costs = 1 carrot <br>
     * - 2 fields cost = 1 + 2 carrots <br>
     * - 3 fields cost = 1 + 2 + 3 carrots <br>
     * ... and so on.
     * @param carrots The amount of carrots to check.
     * @return The maximum distance {@code carrots} can get a {@link Player} to without going into negative carrots.
     */
    @Override
    public int calcMaxReachablePosition(int carrots) {
        /*
         * The total cost to reach a position 'n' is the sum of the first 'n' natural numbers,
         * which is given by the formula: S = n(n + 1)/2, where S is the sum (total cost).
         *
         * To find the maximum position 'n' that can be reached with a given number of carrots 'C',
         * we rearrange the formula to solve for 'n':
         * n(n + 1)/2 = C
         *
         * This is a quadratic equation in the form of n^2 + n - 2C = 0.
         * Solving for 'n' using the quadratic formula (n = [-b ± sqrt(b^2 - 4ac)] / 2a) gives us:
         * n = [-1 ± sqrt(1 + 8C)] / 2
         *
         * Since 'n' can't be negative in this context, we use the positive root:
         * n = [sqrt(1 + 8C) - 1] / 2
         */
        return (int)(Math.sqrt(1 + 8 * (long)carrots) - 1) / 2;
    }

    /**
     * Draws an {@link ActionCard} from the {@link #cards} stack. This changes the {@link #cards} stack.
     * @return The drawn {@link ActionCard}.
     */
    @Override
    public ActionCard drawCard() {
        return cards.draw();
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * Gets the rank of {@code player}.
     * The rank is the position of a {@link Player} against all other players, also counting finished players.
     * @param player The {@link Player} whose rank is to be returned.
     * @return The rank of {@code player}.
     */
    @Override
    public int getRank(Player player) {
        int finishedRank = finishedPlayers.indexOf(player);
        if (finishedRank != -1) {
            return finishedRank + 1;
        }

        return (int) Arrays.stream(players)
                .filter(otherPlayer -> !finishedPlayers.contains(otherPlayer)
                        && otherPlayer.getField() > player.getField())
                .count() + 1 + finishedPlayers.size();
    }


    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * Even if we already have {@link #getStartingCarrots(int)}, we need this for a non-static context.
     * @return The amount of carrots every {@link Player} starts with.
     * @see #getStartingCarrots(int)
     */
    @Override
    public int getStartingCarrots() {
        return getStartingCarrots(players.length);
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * If a {@link Player} can finish is determined by the amount of carrots and salads.
     * @param carrots The amount of carrots to check.
     * @param salads The amount of salads to check.
     * @return {@code true} if the game can finish with the given {@code carrots} and {@code salads}, {@code false} otherwise.
     */
    @Override
    public boolean canFinish(int carrots, int salads) {
        return salads <= 0 && carrots <= getMaxAmountOfCarrotsToEnd();
    }

    /**
     * The {@code playerCount} determines the amount of carrots every {@link Player} starts with.
     * @param playerCount The amount of {@link Player}s in the game.
     * @return The amount of carrots every {@link Player} starts with.
     */
    private static int getStartingCarrots(int playerCount) {
        return playerCount < MIN_PLAYERS_FOR_VARIANT ? STARTING_CARROTS : STARTING_CARROTS_VARIANT;
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * In the game, moving costs increase linearly with each step: <br>
     * - 1 field costs = 1 carrot <br>
     * - 2 fields cost = 1 + 2 carrots <br>
     * - 3 fields cost = 1 + 2 + 3 carrots <br>
     * ... and so on.
     * @param from The field moved from.
     * @param to The field moved to.
     * @return The amount of carrots needed to move from {@code from} to {@code to}.
     * Returns 0 if {@code from} is greater than or equal to {@code to}, since moving backwards is free.
     * @see #calcMaxReachablePosition(int)
     */
    @Override
    public int calcMovementCost(int from, int to) {
        if (from >= to) {
            return 0;
        }

        int distance = to - from;
        // see calcMaxReachablePosition for explanation
        return distance * (distance + 1) / 2;
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * @param field The field to check.
     * @return {@code true} if the given {@code field} is occupied by a {@link Player}, {@code false} otherwise.
     */
    @Override
    public boolean isOccupied(int field) {
        return Arrays.stream(players)
                .anyMatch(player -> player.getField() == field);
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * @param field The field to check.
     * @return The {@link TileRegistry} (type) of the given {@code field}.
     * @see Board#FIELDS
     */
    @Override
    public TileRegistry getTile(int field) {
        return Board.FIELDS[field];
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * Finds the first available field behind the {@link Player} that is one rank behind {@code player}.
     * It's possible to go multiple ranks back if there are no available fields.
     * It's even possible to have to go back to the start field.
     * @param player The {@link Player} whose fallback position is to be returned.
     * @return the first available field behind the {@link Player} that is one rank behind {@code player}.
     * If {@code player} already is in last place, returns the {@code player}'s current position.
     */
    @Override
    public int getFallBackRankPosition(Player player) {
        if (getRank(player) == players.length) {
            return player.getField();
        }

        int backRankPlayerPosition = player.getField() - 1;
        while (backRankPlayerPosition > 0 && !isOccupied(backRankPlayerPosition)) {
            backRankPlayerPosition--;
        }

        for (int i = backRankPlayerPosition - 1; i > 0; i--) {
            boolean isAccessibleSaladTile = !(Board.FIELDS[i] == TileRegistry.SALAD && player.getSalads() < 1);
            if (isAccessibleSaladTile && !isOccupied(i)) {
                return i;
            }
        }

        return 0;
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * Finds the first available field in front of the {@link Player} that is one rank ahead of {@code player}.
     * It's possible to go multiple ranks ahead if there are no available fields.
     * @param player The {@link Player} whose move up position is to be returned.
     * @return the first available field in front of the {@link Player} that is one rank ahead of {@code player}.
     * If {@code player} already is in first place, returns the {@code player}'s current position.
     */
    @Override
    public int getMoveUpRankPosition(Player player) {
        if (getRank(player) == 1) {
            return player.getField();
        }

        int upRankPlayerPosition = player.getField() + 1;
        while (upRankPlayerPosition < Board.size() && !isOccupied(upRankPlayerPosition)) {
            upRankPlayerPosition++;
        }

        for (int i = upRankPlayerPosition + 1; i < Board.size(); i++) {
            boolean isAccessibleSaladTile = !(Board.FIELDS[i] == TileRegistry.SALAD && player.getSalads() < 1);
            if (isAccessibleSaladTile && Board.FIELDS[i] != TileRegistry.HEDGEHOG && !isOccupied(i) ) {
                return i;
            }
        }

        return Board.size() - 1;
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * Finds the first available field in front of {@code player} that is a carrot field.
     * It's possible to go multiple carrot fields ahead if they are occupied.
     * @param player The {@link Player} whose next carrot field is to be returned.
     * @return the first available field in front of {@code player} that is a carrot field.
     * If there are no available carrot fields, returns the {@code player}'s current position.
     */
    @Override
    public int getNextCarrotField(Player player) {
        return IntStream.range(player.getField() + 1, Board.size())
                .filter(i -> Board.FIELDS[i] == TileRegistry.CARROT && !isOccupied(i))
                .findFirst()
                .orElse(player.getField());
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * Finds the first available field behind {@code player} that is a carrot field.
     * It's possible to go multiple carrot fields behind if they are occupied.
     * @param player The {@link Player} whose last carrot field is to be returned.
     * @return the first available field behind {@code player} that is a carrot field.
     * If there are no available carrot fields, returns the {@code player}'s current position.
     */
    @Override
    public int getLastCarrotField(Player player) {
        return IntStream.iterate(player.getField() - 1, i -> i > 0, i -> i - 1)
                .filter(i -> Board.FIELDS[i] == TileRegistry.CARROT && !isOccupied(i))
                .findFirst()
                .orElse(player.getField());
    }

    /**
     * Get a {@link Player} by index.
     * @param index The index of the {@link Player}.
     * @return The {@link Player} at the given index.
     */
    Player getPlayer(int index) {
        return players[index];
    }

    /**
     * Used for {@link GameContext}, since {@link Game} implements it.
     * Gets higher the more {@link Player}s have finished the game.
     * @return max amount of carrots a player can have to finish the game.
     */
    int getMaxAmountOfCarrotsToEnd() { // not private for tests
        return CARROT_EXCHANGE_AMOUNT * (finishedPlayers.size() + 1);
    }

    /**
     * Used for tests.
     * @return The {@link ActionCardStack} of the {@link Game}.
     */
    ActionCardStack getCards() { // not private for tests
        return cards;
    }

    /**
     * Initializes the positions of the players on the board.
     * This is necessary to move the player visuals to the correct positions on the board,
     * since player positions from save games are not necessarily the starting positions.
     * @param playerCount The number of players to initialize.
     * @param onComplete A {@link Runnable} that is called when the initialization is complete.
     */
    private void initializePlayerPositions(int playerCount, Runnable onComplete) {
        // The order of the players is important, to ensure that the player visuals are moved in a correct way.
        // Otherwise, the player visuals could be moved wrongly at the start and end field, which can have
        // multiple players on it.
        if (playerCount > 0) {
            gui.selectPlayerVisual(currentPlayer);
            int field = players[currentPlayer].getField();

            setCurrPlayerToNext();
            gui.movePlayerVisual(() -> initializePlayerPositions(playerCount - 1, onComplete), field, field);
        } else {
            onComplete.run();
        }
    }

    /**
     * Runs the game loop.
     * The game loop is (kind of) a recursive function that runs until the game is over.
     * It starts the turn of the current player and afterward gets called again.
     * When the game is over, it calls {@link #end()}.
     * @see #startTurnOfPlayer(Player, Runnable)
     */
    private void runGameLoop() {
        if (isGameOver()) {
            end();
            return;
        }

        startTurnOfPlayer(players[currentPlayer], () -> {
            Player player = players[currentPlayer];

            LogMessage.endTurn(player.getName());
            if (player.getField() == Board.END_FIELD && !finishedPlayers.contains(player)) {
                finishedPlayers.add(player);
                LogMessage.reachesEnd(player.getName(), getRank(player));
            }

            setCurrPlayerToNext();
            runGameLoop();
        });
    }

    /**
     * Checks if the game is over.
     * @return {@code true} if the game is over, {@code false} otherwise.
     */
    private boolean isGameOver() {
        return finishedPlayers.size() >= players.length;
    }

    /**
     * Ends the game.
     */
    private void end() {
        gui.showGameOver(finishedPlayers.get(0).getName(), () -> {
            LogMessage.gameOver();
            for (Player player : players) {
                LogMessage.isRanked(player.getName(), getRank(player));
            }
        });
    }

    /**
     * Sets the current player to the next player.
     */
    private void setCurrPlayerToNext() {
        currentPlayer = ++currentPlayer % players.length;
    }

    /**
     * Converts the {@link Game} to a JSON string.
     * @return The JSON string.
     * @see #toGameData()
     */
    private String toJson() {
        return toGameData().toJson();
    }
}
