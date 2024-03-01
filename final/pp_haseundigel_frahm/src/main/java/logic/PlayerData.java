package logic;

import com.google.gson.*;
import logic.Logging.LogMessage;

import java.lang.reflect.Type;

/**
 * This class is just a container for the data of a player.
 * It is used to serialize and deserialize the data of a player to and from JSON.
 * This is necessary to get save files that are in the required format and be able
 * to load example save files.
 * @author Lukas Frahm
 */
public class PlayerData implements JsonSerializer<PlayerData>, JsonDeserializer<PlayerData> {
    static final String nameProperty = "name";
    static final String isSuspendedProperty = "suspended";
    static final String isEatingSaladProperty = "eatsSalad";
    static final String fieldProperty = "position";
    static final String carrotsProperty = "carrots";
    static final String saladsProperty = "salads";

    final String name;
    final boolean isSuspended;
    final boolean isEatingSalad;
    final int field;
    final int carrots;
    final int salads;

    PlayerData() {
        name = "";
        isSuspended = isEatingSalad = false;
        field = carrots = salads = 0;
    }

    PlayerData(String name, boolean isSuspended, boolean isEatingSalad, int field, int carrots, int salads) {
        this.name = name;
        this.isSuspended = isSuspended;
        this.isEatingSalad = isEatingSalad;
        this.field = field;
        this.carrots = carrots;
        this.salads = salads;
    }

    @Override
    public JsonElement serialize(PlayerData player, Type type, JsonSerializationContext context) {
        JsonObject jsonPlayer = new JsonObject();

        jsonPlayer.addProperty(nameProperty, player.name);
        jsonPlayer.addProperty(isSuspendedProperty, player.isSuspended);
        jsonPlayer.addProperty(isEatingSaladProperty, player.isEatingSalad);
        jsonPlayer.addProperty(fieldProperty, player.field);
        jsonPlayer.addProperty(carrotsProperty, player.carrots);
        jsonPlayer.addProperty(saladsProperty, player.salads);

        return jsonPlayer;
    }

    @Override
    public PlayerData deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonPlayer = json.getAsJsonObject();

        String name;
        boolean isSuspended;
        boolean isEatingSalad;
        int field, carrots, salads;

        try {
            name = context.deserialize(jsonPlayer.get(nameProperty), String.class);
            isSuspended = context.deserialize(jsonPlayer.get(isSuspendedProperty), boolean.class);
            isEatingSalad = context.deserialize(jsonPlayer.get(isEatingSaladProperty), boolean.class);
            field = context.deserialize(jsonPlayer.get(fieldProperty), int.class);
            carrots = context.deserialize(jsonPlayer.get(carrotsProperty), int.class);
            salads = context.deserialize(jsonPlayer.get(saladsProperty), int.class);
        } catch (NullPointerException | JsonSyntaxException e) {
            LogMessage.error("Save file is not in the correct format...", e);
            return null;
        }

        return new PlayerData(name, isSuspended, isEatingSalad, field, carrots, salads);
    }
}
