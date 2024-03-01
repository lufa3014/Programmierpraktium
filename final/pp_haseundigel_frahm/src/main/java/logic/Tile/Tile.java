package logic.Tile;

import logic.GameContext;
import logic.Player;
import logic.Action;

/**
 * The `Tile` classes represents a field type on the game board.
 * This is the base class for all field types.
 * @see TileRegistry
 *
 * @author Lukas Frahm
 */
abstract class Tile {
    /**
     * We ask a field if it is accessible for a player.
     * @param field The field to check.
     * @param context The game context.
     * @param player The player.
     * @return {@code true} if the field is accessible, {@code false} otherwise.
     */
    boolean isAccessibleTile(int field, GameContext context, Player player) {
        int
                distanceFromPlayer = field - player.getField(),
                maxDistance = context.calcMaxReachablePosition(player.getCarrots());

        return distanceFromPlayer > 0 && distanceFromPlayer <= maxDistance && !context.isOccupied(field);
    }

    /**
     * We ask a field what action can be performed on it.
     * @return The action.
     */
    abstract Action getAction();

    /**
     * We ask a field what action can be performed on it when entering it.
     * @param from The field the player is coming from.
     * @return The action or on complete action if there is no action.
     */
    Action getEntryAction(int from) {
        return (gui, context, player, onComplete) -> onComplete.run();
    }
}