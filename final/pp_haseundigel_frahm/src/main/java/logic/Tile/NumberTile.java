package logic.Tile;

import logic.*;

import java.util.Arrays;

/**
 * The `NumberTile` class represents all number field type on the game board.
 * @see TileRegistry
 *
 * @author Lukas Frahm
 */
class NumberTile extends Tile {
    /**
     * The specific type of the number field.
     */
    private final Type type;

    /**
     * The `Type` enum represents the different number field types.
     */
    enum Type {
        Flag(new int[] { 1, 5, 6 }), Two(new int[] { 2 }), Three(new int[] { 3 }), Four(new int[] { 4 });

        /**
         * The ranks who can benefit from the field.
         */
        private final int[] ranks;

        /**
         * Create a new type.
         * @param ranks The ranks who can benefit from the field.
         */
        Type(int[] ranks) {
            this.ranks = ranks;
        }

        /**
         * Check if the type contains a rank.
         * @param r The rank.
         * @return {@code true} if the type contains the rank, {@code false} otherwise.
         */
        boolean contains(int r) {
            return Arrays.stream(ranks).anyMatch(value -> value == r);
        }
    }

    /**
     * Create a new number field type.
     * @param type The specific type of the number field.
     */
    NumberTile(Type type) {
        this.type = type;
    }

    @Override
    Action getAction() {
        return (gui, context, player, onComplete) -> {
            int rank = context.getRank(player);
            if (type.contains(rank)) {
                int amount = rank * Game.CARROT_EXCHANGE_AMOUNT;
                ActionBuilder.createCarrotExchange(amount, true)
                        .execute(gui, context, player, () ->
                                ActionBuilder.createSelectFieldAndMove().execute(gui, context, player, onComplete)
                        );
            } else {
                ActionBuilder.createSelectFieldAndMove().execute(gui, context, player, onComplete);
            }
        };
    }
}