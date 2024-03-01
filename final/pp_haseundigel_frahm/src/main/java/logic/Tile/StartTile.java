package logic.Tile;

import logic.ActionBuilder;
import logic.GameContext;
import logic.Player;
import logic.Action;

/**
 * The `StartTile` class represents the start field type on the game board.
 * @see TileRegistry
 *
 * @author Lukas Frahm
 */
class StartTile extends Tile {

    @Override
    boolean isAccessibleTile(int field, GameContext context, Player player) {
        return false;
    }

    @Override
    Action getAction() {
        return ActionBuilder.createSelectFieldAndMove();
    }

    @Override
    Action getEntryAction(int from) {
        return (gui, context, player, onComplete) -> {
            int amount = context.getStartingCarrots() - player.getCarrots();
            player.addCarrots(amount);
            onComplete.run();
        };
    }
}