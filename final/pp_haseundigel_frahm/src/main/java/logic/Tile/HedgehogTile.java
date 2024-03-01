package logic.Tile;

import logic.*;

/**
 * The `HedgehogTile` class represents the hedgehog field type on the game board.
 * @see TileRegistry
 *
 * @author Lukas Frahm
 */
class HedgehogTile extends Tile {

    @Override
    boolean isAccessibleTile(int field, GameContext context, Player player) {
        int playerField = player.getField();

        if (field >= playerField || context.isOccupied(field)) {
            return false;
        }

        int firstFind = -1;
        for (int i = playerField - 1; i >= 0; i--) {
            if (Board.FIELDS[i] == TileRegistry.HEDGEHOG) {
                firstFind = i;
                break;
            }
        }

        return firstFind == field;
    }

    @Override
    Action getAction() {
        return ActionBuilder.createSelectFieldAndMove();
    }

    @Override
    Action getEntryAction(int from) {
        return (gui, context, player, onComplete) -> {
            int amount = (from - player.getField()) * Game.CARROT_EXCHANGE_AMOUNT;
            ActionBuilder.createCarrotExchange(amount, true).execute(gui, context, player, onComplete);
        };
    }
}