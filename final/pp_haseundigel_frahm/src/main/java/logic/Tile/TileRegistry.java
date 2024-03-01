package logic.Tile;

import logic.GameContext;
import logic.Player;
import logic.Action;

import static logic.Tile.NumberTile.Type;

/**
 * The `TileRegistry` enum represents the different field types.
 * @see Tile
 *
 * @author Lukas Frahm
 */
public enum TileRegistry {
    START(new StartTile()),
    END(new EndTile()),
    CARROT(new CarrotTile()),
    SALAD(new SaladTile()),
    HARE(new HareTile()),
    HEDGEHOG(new HedgehogTile()),
    TWO(new NumberTile(Type.Two)),
    THREE(new NumberTile(Type.Three)),
    FOUR(new NumberTile(Type.Four)),
    FLAG(new NumberTile(Type.Flag));

    /**
     * The tile object...
     */
    private final Tile object;

    /**
     * Create a new tile registry.
     * @param object The tile object.
     */
    TileRegistry(Tile object) {
        this.object = object;
    }

    /**
     * We ask a field if it is accessible for a player.
     * @param field The field to check.
     * @param context The game context.
     * @param player The player.
     * @return {@code true} if the field is accessible, {@code false} otherwise.
     */
    public boolean isAccessibleTile(int field, GameContext context, Player player) {
        return object.isAccessibleTile(field, context, player);
    }

    /**
     * We ask a field what action can be performed on it.
     * @return The action.
     */
    public Action getAction() {
        return object.getAction();
    }

    /**
     * We ask a field what action can be performed on it when entering it.
     * @param from The field the player is coming from.
     * @return The action or on complete action if there is no action.
     */
    public Action getEntryAction(int from) {
        return object.getEntryAction(from);
    }
}