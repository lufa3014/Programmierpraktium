package logic;

import logic.Tile.TileRegistry;

/**
 * The `GameContext` interface provides information about the game state.
 * Every method in this interface is a pure function, meaning that it does not modify the game state.
 * There is one exception to this rule: {@link #drawCard()}.
 *
 * @author Lukas Frahm
 */
public interface GameContext {

    /**
     * Draws a card from the stack. <br>
     * <br>
     * <b>Warning:</b> This method modifies the game state by putting the drawn card at the bottom of the stack.
     * But this is the only exception, and it is only done because it is very convenient to do so.
     * If we wouldn't do this, we would have to keep track of the number of drawn cards in a turn,
     * because a player could draw multiple cards in a turn, and this would cause all kinds of
     * problems to solve in the current implementation. Having this one exception makes it much easier.
     * @return the drawn card.
     * @see ActionCardStack#draw()
     */
    ActionCard drawCard();

    /**
     * Get the `field type` of a field.
     * @param field The field to get the type of.
     * @return The type of the field.
     */
    TileRegistry getTile(int field);

    /**
     * Get the rank of a player.
     * @param player The player to get the rank of.
     * @return The rank of the player.
     */
    int getRank(Player player);

    /**
     * Get the number of carrots every player started with.
     * @return The number of carrots every player started with.
     */
    int getStartingCarrots();

    /**
     * Looks at the carrots and salads and determines if a player can enter the end with them.
     * @param carrots The number of carrots used to determine if the player can finish.
     * @param salads The number of salads used to determine if the player can finish.
     * @return {@code true} if a player can finish with them, {@code false} otherwise.
     */
    boolean canFinish(int carrots, int salads);

    /**
     * calculates the movement cost between 'from' and 'to'.
     * @param from The field to move from.
     * @param to The field to move to.
     * @return The movement cost between 'from' and 'to'.
     */
    int calcMovementCost(int from, int to);

    /**
     * Calculates the maximum reachable position with the given amount of carrots.
     * @param carrots The amount of carrots to use for calculating the maximum reachable position.
     * @return The maximum reachable position with the given amount of carrots.
     */
    int calcMaxReachablePosition(int carrots);

    /**
     * Finds all available fields for the given player.
     * @param player The player to find the available fields for.
     * @return An array of all available fields for the given player. sorted ascending.
     */
    int[] getAvailableFields(Player player);

    /**
     * Gets the default amount for a carrot exchange.
     * @return The default amount for a carrot exchange.
     */
    int getCarrotExchangeAmount();

    /**
     * Whether the given field is occupied by a player or not.
     * @param field The field to check.
     * @return {@code true} if the field is occupied, {@code false} otherwise.
     */
    boolean isOccupied(int field);

    /**
     * Finds the first available field behind the {@link Player} that is one rank behind {@code player}.
     * It's possible to go multiple ranks back if there are no available fields.
     * It's even possible to have to go back to the start field.
     * @param player The {@link Player} whose fallback position is to be returned.
     * @return the first available field behind the {@link Player} that is one rank behind {@code player}.
     * If {@code player} already is in last place, returns the {@code player}'s current position.
     */
    int getFallBackRankPosition(Player player);

    /**
     * Finds the first available field in front of the {@link Player} that is one rank ahead of {@code player}.
     * It's possible to go multiple ranks ahead if there are no available fields.
     * @param player The {@link Player} whose move up position is to be returned.
     * @return the first available field in front of the {@link Player} that is one rank ahead of {@code player}.
     * If {@code player} already is in first place, returns the {@code player}'s current position.
     */
    int getMoveUpRankPosition(Player player);

    /**
     * Finds the first available field in front of {@code player} that is a carrot field.
     * It's possible to go multiple carrot fields ahead if they are occupied.
     * @param player The {@link Player} whose next carrot field is to be returned.
     * @return the first available field in front of {@code player} that is a carrot field.
     * If there are no available carrot fields, returns the {@code player}'s current position.
     */
    int getNextCarrotField(Player player);

    /**
     * Finds the first available field behind {@code player} that is a carrot field.
     * It's possible to go multiple carrot fields behind if they are occupied.
     * @param player The {@link Player} whose last carrot field is to be returned.
     * @return the first available field behind {@code player} that is a carrot field.
     * If there are no available carrot fields, returns the {@code player}'s current position.
     */
    int getLastCarrotField(Player player);
}
