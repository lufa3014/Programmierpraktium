package logic;

/**
 * The `ActionCard` enum represents the different action cards.
 * @see ActionCardStack
 */
public enum ActionCard {
    ExchangeCarrots,
    ConsumeSalad,
    GetSuspended,
    TakeTurnAgain,
    FreeLastMove,
    MoveUpRank,
    FallBackRank,
    MoveToNextCarrotField,
    MoveToLastCarrotField;

    /**
     * <b>ONLY FOR TESTING</b><br>
     * Executes the action card.
     * Assumes that the player didn't move from anywhere.
     */
    public void execute(GUIConnector gui, GameContext context, Player player) {
        execute(gui, context, player, 0);
    }

    /**
     * <b>ONLY FOR TESTING</b><br>
     * Executes the action card.
     */
    public void execute(GUIConnector gui, GameContext context, Player player, int from) {
        ActionBuilder.convertActionCard(this, from).execute(gui, context, player, Game.NO_OP);
    }
}
