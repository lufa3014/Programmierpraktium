package logic;

import logic.Tile.TileRegistry;

import java.util.stream.IntStream;

import static logic.Tile.TileRegistry.*;

/**
 * The `Board` class represents the game board. <br>
 * It defines the layout of tiles on the board and provides utility methods
 * to get information about the board.
 *
 * @author Lukas Frahm
 */
public class Board {
    /**
     * An array representing the layout of tiles on the game board.
     * Each element in the array corresponds to a tile on the board.
     * The order of tiles is important for gameplay.
     * @see TileRegistry
     */
    public static final TileRegistry[] FIELDS = new TileRegistry[] {
            START, HARE, CARROT, HARE, THREE, CARROT, HARE, SALAD, HEDGEHOG, FOUR, TWO, HEDGEHOG, THREE, CARROT, HARE,
            HEDGEHOG, FLAG, TWO, FOUR, HEDGEHOG, THREE, CARROT, SALAD, TWO, HEDGEHOG, HARE, CARROT, FOUR, THREE, TWO,
            HEDGEHOG, HARE, FLAG, CARROT, HARE, TWO, THREE, HEDGEHOG, CARROT, HARE, CARROT, TWO, SALAD, HEDGEHOG, THREE,
            FOUR, HARE, TWO, FLAG, CARROT, HEDGEHOG, HARE, THREE, TWO, FOUR, CARROT, HEDGEHOG, SALAD, HARE, CARROT, TWO,
            HARE, SALAD, HARE, END
    };

    /**
     * The index of the "START" tile on the board.
     */
    public static final int START_FIELD = IntStream.range(0, Board.FIELDS.length)
            .filter(i -> Board.FIELDS[i] == TileRegistry.START)
            .findFirst()
            .orElse(0);

    /**
     * The index of the "END" tile on the board.
     */
    public static final int END_FIELD = IntStream.range(0, Board.FIELDS.length)
            .filter(i -> Board.FIELDS[i] == TileRegistry.END)
            .findFirst()
            .orElse(Board.FIELDS.length - 1);

    /**
     * Get the total number of tiles on the board.
     * @return The number of tiles on the board.
     */
    public static int size() {
        return FIELDS.length;
    }

    /**
     * Check if a given field index is within the bounds of the board.
     * @param field The field index to check.
     * @return {@code true} if the field is within bounds, {@code false} otherwise.
     */
    public static boolean isInBounds(int field) {
        return field >= 0 && field < size();
    }

    /**
     * Check if a tile at a given field index is accessible by a player in the game context.
     * @param field   The field index to check.
     * @param context The game context.
     * @param player  The player trying to access the tile.
     * @return {@code true} if the tile is accessible, {@code false} otherwise or if not within bounds.
     * @see TileRegistry#isAccessibleTile(int, GameContext, Player)
     */
    public static boolean isAccessibleTile(int field, GameContext context, Player player) {
        return isInBounds(field) && FIELDS[field].isAccessibleTile(field, context, player);
    }
}
