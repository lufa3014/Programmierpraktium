package logic.Tile;

import logic.Action;
import logic.ActionBuilder;

/**
 * The `HareTile` class represents the hare field type on the game board.
 * @see TileRegistry
 *
 * @author Lukas Frahm
 */
class HareTile extends Tile {

    @Override
    Action getAction() {
        return ActionBuilder.createSelectFieldAndMove();
    }

    @Override
    Action getEntryAction(int from) {
        return ActionBuilder.createDrawActionCard(from);
    }
}
