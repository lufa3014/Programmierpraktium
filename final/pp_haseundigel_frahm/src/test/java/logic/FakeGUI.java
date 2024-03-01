package logic;


public class FakeGUI implements GUIConnector {

    public String[] defaultNames = {"Player 1", "Player 2", "Player 3", "Player 4", "Player 5", "Player 6"};

    @Override
    public void updatePlayerName(String name) {

    }

    @Override
    public void updatePlayerCarrots(int carrots) {

    }

    @Override
    public void updatePlayerSalads(int salads) {

    }

    @Override
    public void enableMoveSelection(int[] fields, int carrots, int[] carrotCosts, OnFieldSelected observer) {
    }


    @Override
    public void selectPlayerVisual(int playerIndex) {

    }

    @Override
    public void skipPlayerVisualOnStartField(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void movePlayerVisual(Runnable onComplete, int from, int to) {
        onComplete.run();
    }

    @Override
    public void showCarrotExchange(int amount, boolean isAddition, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showCarrotExchangeDecision(int amount, Runnable onAdd, Runnable onRemove, Runnable onMove, boolean showOnRemove) {
        onMove.run();
    }

    @Override
    public void showEatingSalad(String playerName, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showToNextCarrotFieldCard(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showToLastCarrotFieldCard(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showTakeTurnAgainCard(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showConsumeSaladCard(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showMoveUpRankCard(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showFallBackRankCard(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showGetSuspendedCard(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showFreeLastMoveCard(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showCarrotExchangeCard(int exchangeAmount, Runnable onAdd, Runnable onRemove, Runnable onComplete, boolean showOnRemove) {
        onComplete.run();
    }

    @Override
    public void showCantMoveUpRankToEnd(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showAlreadyLastRank(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showNoCarrotFieldToMoveTo(String name, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showNoSaladsToConsume(String name, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showAlreadyFirstRank(Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showGameOver(String winnerName, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showIsSuspended(String name, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showNoValidField(String name, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showReachedEnd(int rank, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showNoCarrotsBackToStart(String name, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    public void showSavingFailed() {

    }
}
