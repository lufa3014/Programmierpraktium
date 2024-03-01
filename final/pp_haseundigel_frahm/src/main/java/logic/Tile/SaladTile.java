package logic.Tile;

import logic.*;
import logic.Logging.LogMessage;

/**
 * The `SaladTile` class represents the salad field type on the game board.
 * @see TileRegistry
 *
 * @author Lukas Frahm
 */
class SaladTile extends Tile {

    @Override
    boolean isAccessibleTile(int field, GameContext context, Player player) {
        return super.isAccessibleTile(field, context, player) && player.getSalads() > 0;
    }

    @Override
    Action getAction() {
        return ActionBuilder.createSelectFieldAndMove();
    }

    @Override
    Action getEntryAction(int from) {
        return (gui, context, player, onComplete) -> {
            player.setEatsSalad(true);
            LogMessage.isEatingSalad(player.getName(), true);
            onComplete.run();
        };
    }
}