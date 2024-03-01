package logic.Tile;

import logic.GameContext;
import logic.Action;
import logic.Player;

/**
 * The `EndTile` class represents the end field type on the game board.
 * @see TileRegistry
 *
 * @author Lukas Frahm
 */
class EndTile extends Tile {

    @Override
    boolean isAccessibleTile(int field, GameContext context, Player player) {
        int
                from = player.getField(),
                carrots = player.getCarrots(),

                distanceFromPlayer = field - from,
                maxDistance = context.calcMaxReachablePosition(carrots),
                carrotsLeft = carrots - context.calcMovementCost(from, field);

        return distanceFromPlayer > 0
                && distanceFromPlayer <= maxDistance
                && context.canFinish(carrotsLeft, player.getSalads());
    }

    @Override
    Action getAction() {
        return (gui, context, player, onComplete) -> onComplete.run();
    }

    @Override
    Action getEntryAction(int from) {
        return (gui, context, player, onComplete) -> gui.showReachedEnd(context.getRank(player), onComplete);
    }
}