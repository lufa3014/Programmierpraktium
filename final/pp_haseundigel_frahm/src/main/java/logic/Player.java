package logic;

/**
 * The `Player` class represents a single player within the game, storing essential player information.
 * This includes the player's current position on the board, as well as their inventory of carrots and salads.
 * <br>
 * Additionally, it keeps track of the player's current state,
 * indicating whether they are suspended or eating a salad.
 *
 * @author Lukas Frahm
 */
public class Player {

    // ==========================================================
    // Instance Variables
    // ==========================================================

    /**
     * The name of the player.
     */
    private final String name;

    /**
     * The field (position) the player is currently on.
     */
    private int field;

    /**
     * The number of carrots the player has.
     */
    private int carrots;

    /**
     * The number of salads the player has.
     */
    private int salads;

    /**
     * Whether the player is suspended.
     */
    private boolean suspended;

    /**
     * Whether the player is eating a salad.
     * If {@code true}, the player will eat a salad next turn.
     */
    private boolean eatsSalad;

    /**
     * A callback that is called when the number of carrots changes.
     * Useful for UI updates.
     */
    private transient Runnable onCarrotsChanged = Game.NO_OP;

    /**
     * A callback that is called when the number of salads changes.
     * Useful for UI updates.
     */
    private transient Runnable onSaladsChanged = Game.NO_OP;


    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * Create a new player with the specified attributes. This is the main constructor.
     * @param name      The name of the player.
     * @param field     The field (position) the player is currently on.
     * @param carrots   The number of carrots the player has.
     * @param salads    The number of salads the player has.
     * @param suspended Whether the player is suspended.
     * @param eatsSalad Whether the player eats salad.
     */
    Player(String name, int field, int carrots, int salads, boolean suspended, boolean eatsSalad) {
        this.name = name;
        this.field = field;
        this.carrots = carrots;
        this.salads = salads;
        this.suspended = suspended;
        this.eatsSalad = eatsSalad;
    }

    /**
     * Create a new player with the specified attributes and default suspension and salad eating settings.<br>
     * <br>
     * This is a convenience constructor. <br>
     * {@link #eatsSalad} and {@link #suspended} are set to {@code false}.
     * @param name     The name of the player.
     * @param field    The field (position) the player is currently on.
     * @param carrots  The number of carrots the player has.
     * @param salads   The number of salads the player has.
     */
    Player(String name, int field, int carrots, int salads) {
        this(name, field, carrots, salads, false, false);
    }

    Player(PlayerData playerData) {
        this(playerData.name, playerData.field, playerData.carrots, playerData.salads, playerData.isSuspended, playerData.isEatingSalad);
    }

    // ==========================================================
    // Methods
    // ==========================================================

    /**
     * Set a runnable to be executed when the number of carrots changes.<br>
     * <br>
     * <b>Warning:</b> This overrides the previous callback.
     * @param onCarrotsChanged The runnable to execute.
     */
    public void setOnCarrotsChanged(Runnable onCarrotsChanged) {
        this.onCarrotsChanged = onCarrotsChanged;
    }

    /**
     * Set a runnable to be executed when the number of salads changes.<br>
     * <br>
     * <b>Warning:</b> This overrides the previous callback.
     * @param onSaladsChanged The runnable to execute.
     */
    public void setOnSaladsChanged(Runnable onSaladsChanged) {
        this.onSaladsChanged = onSaladsChanged;
    }

    /**
     * Just a normal getter. ¯\_(ツ)_/¯
     * @return The field the player is currently on.
     */
    public int getField() {
        return field;
    }

    /**
     * Just a normal setter. ¯\_(ツ)_/¯
     * @param field The field the player is currently on.
     */
    public void setField(int field) {
        this.field = field;
    }

    /**
     * Just a normal getter. ¯\_(ツ)_/¯
     * @return {@code true} if the player is suspended, {@code false} otherwise.
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * Just a normal setter. ¯\_(ツ)_/¯
     * @param suspended {@code true} if the player shall be suspended, {@code false} otherwise.
     */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    /**
     * Just a normal getter. ¯\_(ツ)_/¯
     * @return {@code true} if the player is eating a salad, {@code false} otherwise.
     */
    public boolean isEatingSalad() {
        return eatsSalad;
    }

    /**
     * Just a normal setter. ¯\_(ツ)_/¯
     * @param eatsSalad {@code true} if the player should eat a salad next turn, {@code false} otherwise.
     */
    public void setEatsSalad(boolean eatsSalad) {
        this.eatsSalad = eatsSalad;
    }

    /**
     * Just a normal getter. ¯\_(ツ)_/¯
     * @return The number of carrots the player has.
     */
    public int getCarrots() {
        return carrots;
    }

    /**
     * Add carrots to the player's carrot count. <br>
     * This will also call the {@link #onCarrotsChanged} callback.
     * @param count The number of carrots to add.
     * @see #removeCarrots(int)
     * @see #onCarrotsChanged
     */
    public void addCarrots(int count) {
        carrots += count;
        onCarrotsChanged.run();
    }

    /**
     * Remove carrots from the player's carrot count.
     * The players carrots will never be less than {@code 0}.<br>
     * This will also call the {@link #onCarrotsChanged} callback.
     * @param count The number of carrots to remove.
     * @see #addCarrots(int)
     * @see #onCarrotsChanged
     */
    public void removeCarrots(int count) {
        carrots -= count;
        if (carrots < 0) {
            carrots = 0;
        }

        onCarrotsChanged.run();
    }

    /**
     * Just a normal getter. ¯\_(ツ)_/¯
     * @return The number of salads the player has.
     */
    public int getSalads() {
        return salads;
    }

    /**
     * Remove a salad from the player's salad count.
     * The players salads will never be less than {@code 0}.<br>
     * This will also call the {@link #onSaladsChanged} callback.
     * @see #onSaladsChanged
     */
    public void consumeSalad() {
        salads--;
        if (salads < 0) {
            salads = 0;
        }

        onSaladsChanged.run();
    }

    /**
     * Just a normal getter. ¯\_(ツ)_/¯
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Create a {@link PlayerData} object from this player. Useful for saving the game.
     * @return The {@link PlayerData} object.
     * @see PlayerData
     */
    public PlayerData toPlayerData() {
        return new PlayerData(name, suspended, eatsSalad, field, carrots, salads);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player other)) {
            return false;
        }

        return name.equals(other.name)
                && field == other.field
                && carrots == other.carrots
                && salads == other.salads
                && suspended == other.suspended
                && eatsSalad == other.eatsSalad;
    }

    /**
     * Remove all salads from the player's salad count.<br>
     */
    void consumeAllSalads() {
        salads = 0;
        onSaladsChanged.run();
    }
}