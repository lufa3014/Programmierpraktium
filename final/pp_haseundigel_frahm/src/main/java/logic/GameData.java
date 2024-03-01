package logic;

import com.google.gson.*;
import logic.Logging.LogMessage;
import logic.Tile.TileRegistry;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is just a container for the data of a game.
 * It is used to serialize and deserialize the data of a game to and from JSON.
 * This is necessary to get save files that are in the required format and be able
 * to load example save files.
 * @author Lukas Frahm
 */
public class GameData implements JsonSerializer<GameData>, JsonDeserializer<GameData> {
    private static final boolean MATCH_JSON_EXACTLY_TO_EXAMPLE = true;

    static String playersProperty = "players";
    static String finishedPlayersProperty = "onTarget";
    static String currentPlayerProperty = "currPlayer";

    final Player[] players;
    final int[] finishedPlayers;
    final int currentPlayer;

    GameData() {
        players = new Player[0];
        finishedPlayers = new int[0];
        currentPlayer = 0;
    }

    GameData(Player[] players, int[] finishedPlayers, int currentPlayer) {
        this.players = players;
        this.finishedPlayers = finishedPlayers;
        this.currentPlayer = currentPlayer;
    }

    public int getPlayerCount() {
        return players.length;
    }

    public static GameData getGameDataFromSaveFile(File saveFile) {
        if (saveFile == null) {
            LogMessage.error("Save file is null", new IllegalArgumentException());
            return null;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GameData.class, new GameData())
                .registerTypeAdapter(PlayerData.class, new PlayerData())
                .create();

        GameData result;
        try (Reader reader = new FileReader(saveFile)) {
           result = gson.fromJson(reader, GameData.class);

            if (result != null && !result.validate()) {
                LogMessage.error(
                        "Save file is not valid: " + saveFile.getAbsolutePath(),
                        new IllegalArgumentException()
                );

                result = null;
            }
        } catch (FileNotFoundException e) {
            LogMessage.error("Save file not found: " + saveFile.getAbsolutePath(), e);
            result = null;
        } catch (IOException | JsonIOException e) {
            LogMessage.error("Error while reading save file: " + saveFile.getAbsolutePath(), e);
            result = null;
        } catch (JsonSyntaxException e) {
            LogMessage.error("Save file is not in the correct format: " + saveFile.getAbsolutePath(), e);
            result = null;
        }

        return result;
    }

    @Override
    public JsonElement serialize(GameData game, Type type, JsonSerializationContext context) {
        JsonObject gameJson = new JsonObject();

        gameJson.addProperty(currentPlayerProperty, game.currentPlayer);
        gameJson.add(finishedPlayersProperty, context.serialize(game.finishedPlayers));
        gameJson.add(playersProperty, context.serialize(game.players));

        return gameJson;
    }

    @Override
    public GameData deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject gameJson = json.getAsJsonObject();

        PlayerData[] playerData;
        int[] finishedPlayers;
        int currentPlayer;
        try {
            playerData = context.deserialize(gameJson.get(playersProperty), PlayerData[].class);
            finishedPlayers = context.deserialize(gameJson.get(finishedPlayersProperty), int[].class);
            currentPlayer = context.deserialize(gameJson.get(currentPlayerProperty), int.class);
        } catch (NullPointerException | JsonSyntaxException e) {
            LogMessage.error("Save file is not in the correct format...", e);
            return null;
        }


        Player[] players = Arrays.stream(playerData)
                .map(Player::new)
                .toArray(Player[]::new);

        return new GameData(players, finishedPlayers, currentPlayer);
    }

    public String toJson() {
        Gson prettyGson = new GsonBuilder()
                .registerTypeAdapter(GameData.class, new GameData())
                .registerTypeAdapter(PlayerData.class, new PlayerData())
                .setPrettyPrinting().create();
        String gameJson = prettyGson.toJson(this);

        if (MATCH_JSON_EXACTLY_TO_EXAMPLE) {
            Gson compactGson = new GsonBuilder()
                    .registerTypeAdapter(PlayerData.class, new PlayerData())
                    .create();

            PlayerData[] playerJsons = Arrays.stream(players)
                    .map(Player::toPlayerData)
                    .toArray(PlayerData[]::new);

            String[] playerJsonStrings = Arrays.stream(playerJsons)
                    .map(compactGson::toJson)
                    .map(s -> "    " + s.replace(":", ": ").replace(",", ", "))
                    .toArray(String[]::new);

            String playersJson = String.join(",\n", playerJsonStrings);
            gameJson = gameJson.replaceFirst("\"players\": \\[(?s).*?]", "\"players\": [\n" + playersJson + "\n  ]");
        }

        return gameJson;
    }

    private boolean validate() {
        if (players == null || finishedPlayers == null) {
            return false;
        }

        boolean isValid = players.length >= 2 && players.length <= 6;
        isValid &= finishedPlayers.length <= players.length;
        isValid &= currentPlayer >= 0 && currentPlayer < players.length;

        Set<Integer> finishedPlayerSet = new HashSet<>();
        for (int f : finishedPlayers) {
            isValid &= f >= 0 && f < players.length && finishedPlayerSet.add(f);
        }

        Set<Integer> visitedFields = new HashSet<>();
        for (Player p : players) {
            if (!isValid) break;

            int currentField = p.getField();
            boolean isFieldValid = (currentField == Board.START_FIELD || currentField == Board.END_FIELD)
                    || visitedFields.add(currentField);
            isFieldValid &= currentField >= Board.START_FIELD && currentField < Board.size();

            boolean isPlayerValid = p.getCarrots() >= 0 && p.getSalads() >= 0;

            TileRegistry tile = Board.FIELDS[currentField];
            isPlayerValid &= !p.isEatingSalad() || tile == TileRegistry.SALAD || tile == TileRegistry.HARE;
            isPlayerValid &= !p.isSuspended() || tile == TileRegistry.HARE;

            isValid = isFieldValid && isPlayerValid;
        }

        return isValid;
    }
}
