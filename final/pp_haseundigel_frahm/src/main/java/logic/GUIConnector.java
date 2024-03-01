package logic;

/**
 * The `GUIConnector` interface represents a connector to the GUI.
 * It provides methods to update the GUI and to show messages to the user.
 *
 * @author Lukas Frahm
 */
public interface GUIConnector {

    /**
     * Update the name of the current player.
     * @param name The name of the new current player.
     */
    void updatePlayerName(String name);

    /**
     * Update the number of carrots of the current player.
     * @param carrots The number of carrots of the new current player.
     */
    void updatePlayerCarrots(int carrots);

    /**
     * Update the number of salads of the current player.
     * @param salads The number of salads of the new current player.
     */
    void updatePlayerSalads(int salads);

    /**
     * Activate a field selection on the GUI. The callback is called when a field is selected,
     * with the selected field as the parameter.
     * @param fields The fields to enable selection for.
     * @param carrots The number of carrots the player has.
     * @param carrotCosts The number of carrots each field costs. (Must be the same length as fields)
     * @param observer The callback to call when a field is selected. Receives the selected field as a parameter.
     */
    void enableMoveSelection(int[] fields, int carrots, int[] carrotCosts, OnFieldSelected observer);

    /**
     * Select a player on the GUI.
     * @param playerIndex The index of the player to select.
     */
    void selectPlayerVisual(int playerIndex);

    /**
     * Moves the player from the most right field of start to the left most field of start.
     * Necessary to not disturb the natural order of the start field.
     * @param onComplete A callback to call when the animation is completed.
     */
    void skipPlayerVisualOnStartField(Runnable onComplete);

    /**
     * Move the player on the GUI. It uses the player that got selected with {@link #selectPlayerVisual(int)}.
     * @param onComplete A callback to call when the animation is completed.
     * @param from The field to move from.
     * @param to The field to move to.
     */
    void movePlayerVisual(Runnable onComplete, int from, int to);

    /**
     * messages the user that a carrot are added or removed.
     * @param amount The amount of carrots to add or remove.
     * @param isAddition Whether the carrots are added or removed.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showCarrotExchange(int amount, boolean isAddition, Runnable onComplete);

    /**
     * shows the user a decision to add or remove carrots or to move. <br>
     * The user has to choose one of the options.
     * @param amount The amount of carrots to add or remove.
     * @param onAdd A callback to call when the user chooses to add carrots.
     * @param onRemove A callback to call when the user chooses to remove carrots.
     * @param onMove A callback to call when the user chooses to move.
     * @param showOnRemove Whether to show the remove option. If false, the remove option will be hidden.
     *                     This is useful when the player has not enough carrots to remove.
     */
    void showCarrotExchangeDecision(int amount, Runnable onAdd, Runnable onRemove, Runnable onMove, boolean showOnRemove);

    /**
     * messages the user that he has to eat a salad.
     * @param playerName The name of the player that has to eat a salad.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showEatingSalad(String playerName, Runnable onComplete);

    /**
     * messages the user that he has drawn the card {@link ActionCard#MoveToNextCarrotField}.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showToNextCarrotFieldCard(Runnable onComplete);

    /**
     * messages the user that he has drawn the card {@link ActionCard#MoveToLastCarrotField}.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showToLastCarrotFieldCard(Runnable onComplete);

    /**
     * messages the user that he has drawn the card {@link ActionCard#TakeTurnAgain}.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showTakeTurnAgainCard(Runnable onComplete);

    /**
     * messages the user that he has drawn the card {@link ActionCard#ConsumeSalad}.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showConsumeSaladCard(Runnable onComplete);

    /**
     * messages the user that he has drawn the card {@link ActionCard#MoveUpRank}.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showMoveUpRankCard(Runnable onComplete);

    /**
     * messages the user that he has drawn the card {@link ActionCard#FallBackRank}.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showFallBackRankCard(Runnable onComplete);

    /**
     * messages the user that he has drawn the card {@link ActionCard#GetSuspended}.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showGetSuspendedCard(Runnable onComplete);

    /**
     * messages the user that he has drawn the card {@link ActionCard#FreeLastMove}.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showFreeLastMoveCard(Runnable onComplete);

    /**
     * messages the user that he has drawn the card {@link ActionCard#ExchangeCarrots}
     * and shows the user a decision to add or remove carrots or to do nothing.
     * The user has to choose one of the options.
     * @param amount The amount of carrots to add or remove.
     * @param onAdd A callback to call when the user chooses to add carrots.
     * @param onRemove A callback to call when the user chooses to remove carrots.
     * @param onNothing A callback to call when the user chooses to do nothing.
     * @param showOnRemove Whether to show the remove option. If false, the remove option will be hidden.
     *                     This is useful when the player has not enough carrots to remove.
     */
    void showCarrotExchangeCard(int amount, Runnable onAdd, Runnable onRemove, Runnable onNothing, boolean showOnRemove);

    /**
     * shows the user that he can't move up a rank because till end there are no available fields.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showCantMoveUpRankToEnd(Runnable onComplete);

    /**
     * shows the user that he can't move up a rank because he is already last rank.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showAlreadyLastRank(Runnable onComplete);

    /**
     * shows the user that he can't move to the next carrot field because there are no available carrot fields.
     * @param name The name of the player that can't move to the next carrot field.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showNoCarrotFieldToMoveTo(String name, Runnable onComplete);

    /**
     * shows the user that he can't consume a salad because he has no salads.
     * @param name The name of the player that can't consume a salad.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showNoSaladsToConsume(String name, Runnable onComplete);

    /**
     * shows the user that he can't move up a rank because he is already first rank.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showAlreadyFirstRank(Runnable onComplete);

    /**
     * messages the user that the game is over and shows the winner.
     * @param winnerName The name of the winner.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showGameOver(String winnerName, Runnable onComplete);

    /**
     * messages the user that he is suspended.
     * @param name The name of the player that is suspended.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showIsSuspended(String name, Runnable onComplete);

    /**
     * messages the user that he can't do anything because there are no available fields.
     * @param name The name of the player that can't do anything.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showNoValidField(String name, Runnable onComplete);

    /**
     * messages the user that he has reached the end and shows his rank.
     * @param rank The rank of the player.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showReachedEnd(int rank, Runnable onComplete);

    /**
     * messages the user that he can't move to the last carrot field because there are no available carrot fields.
     * @param name The name of the player that can't move to the last carrot field.
     * @param onComplete A callback to call when user has confirmed the message.
     */
    void showNoCarrotsBackToStart(String name, Runnable onComplete);

    /**
     * messages the user that saving the game failed.
     */
    void showSavingFailed();
}
