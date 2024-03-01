package logic.Tile;

import logic.Action;
import logic.ActionBuilder;

/**
 * The `CarrotTile` class represents the carrot field type on the game board.
 * @see TileRegistry
 *
 * @author Lukas Frahm
 */
class CarrotTile extends Tile {

    @Override
    Action getAction() {
        return ActionBuilder.createCarrotExchangeDecision();
    }
}