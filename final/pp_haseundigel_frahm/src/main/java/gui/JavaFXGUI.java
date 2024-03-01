package gui;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import logic.GUIConnector;
import logic.Logging.LogMessage;
import logic.OnFieldSelected;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to connect the game logic to the GUI.
 * It is used to update the GUI, move the player visuals and show alerts.<br>
 * <br>
 * This class is part of a callback-system between the game logic and the GUI.
 *
 * @author Lukas Frahm
 */
class JavaFXGUI implements GUIConnector {

    // ==========================================================
    // Constants
    // ==========================================================

    /**
     * The color used to highlight the player that is currently on turn.
     */
    private static final Color PLAYER_SELECTION_COLOR       = Color.LIGHTSKYBLUE;

    /**
     * The color used to highlight a field that can be moved to.
     */
    private static final Color MOVE_SELECTION_COLOR_VALID   = Color.GREENYELLOW;

    /**
     * The color used to highlight a field that cannot be moved to.
     */
    private static final Color MOVE_SELECTION_COLOR_INVALID = Color.RED;

    // ==========================================================
    // Instance Variables
    // ==========================================================

    /**
     * The clickable fields of the game board.
     */
    private final Rectangle[] clickableFields;

    /**
     * The player visuals. The index of a player visual corresponds to the index of the player in the game.
     */
    private final PlayerVisual[] players;

    /**
     * The label that shows the name of the player that's turn it turn.
     */
    private final Label playerName;

    /**
     * The label that shows the number of carrots the player that's turn it has.
     */
    private final Label playerCarrots;

    /**
     * The label that shows the number of salads the player that's turn it is has.
     */
    private final Label playerSalads;

    /**
     * The game alert that is used to show alerts.<br>
     * It acts as a blueprint for the alerts that are shown.
     */
    private final GameAlert gameAlert;

    /**
     * Whether the GUI is still alive.
     * This is used to prevent the GUI from being used after it has been destroyed.
     */
    private boolean isAlive = true;

    /**
     * The number of start positions that are occupied.
     * This is used to move players in the starting position.
     */
    private int occupiedStartPositions;

    /**
     * The number of end positions that are occupied.
     * This is used to move players to the right end position.
     */
    private int occupiedEndPositions;

    /**
     * The player visual that is currently selected.
     * This is the player visual that is highlighted and used for moving.
     */
    private PlayerVisual selectedPlayer;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * Create a new JavaFXGUI.
     * @param clickableFields The clickable fields of the game board.
     * @param players The player visuals. The index of a player visual corresponds to the index of the player in the game.
     * @param playerName The label that shows the name of the player that's turn it turn.
     * @param playerCarrots The label that shows the number of carrots the player that's turn it has.
     * @param playerSalads The label that shows the number of salads the player that's turn it is has.
     * @param gameAlert The game alert that is used to show alerts.
     */
    JavaFXGUI(Rectangle[] clickableFields, PlayerVisual[] players, Label playerName, Label playerCarrots, Label playerSalads, GameAlert gameAlert) {
        this.clickableFields = clickableFields;
        this.players = players;
        this.playerName = playerName;
        this.playerCarrots = playerCarrots;
        this.playerSalads = playerSalads;
        this.gameAlert = gameAlert;
    }

    // ==========================================================
    // Methods
    // ==========================================================

    @Override
    public void updatePlayerName(String name) {
            playerName.setText("Am Zug: " + name);
            playerName.setTextFill(selectedPlayer.getColor());
    }

    @Override
    public void updatePlayerCarrots(int carrots) {
            playerCarrots.setText("Karotten: " + carrots);
    }

    @Override
    public void updatePlayerSalads(int salads) {
            playerSalads.setText("Salate: " + salads);
    }

    @Override
    public void enableMoveSelection(int[] fields, int carrots, int[] carrotCosts, OnFieldSelected observer) {
        // Iterate over all fields except the finish line
        for (int i = 0; i < clickableFields.length - 1; i++) {
            Rectangle field = clickableFields[i];

            field.setOnMouseEntered(mouseEvent -> field.setStroke(MOVE_SELECTION_COLOR_INVALID));
            field.setOnMouseExited(mouseEvent -> resetFieldAppearance(field, carrots));
        }

        for (int i = 0; i < fields.length; i++) {
            int fieldNumber = fields[i];
            if (fieldNumber >= 1 && fieldNumber <= clickableFields.length) {
                Rectangle clickableField = clickableFields[fieldNumber - 1];
                setupFieldInteraction(clickableField, fieldNumber, (carrots - carrotCosts[i]), observer);
            } else {
                LogMessage.error(
                        "Field " + fieldNumber + " does not exist in the GUI",
                        new IllegalArgumentException()
                );
            }
        }
    }


    @Override
    public void skipPlayerVisualOnStartField(Runnable onComplete) {
        List<Point2D> pathPointsList = new ArrayList<>();
        pathPointsList.add(DataProvider.startPositions[0]);
        pathPointsList.add(DataProvider.startPositions[occupiedStartPositions]);

        // necessary because otherwise we might get artifacts when creating and using a new JavaFXGUI
        Runnable onCompleteWithIsAliveCheck = () -> {
            if (isAlive) {
                onComplete.run();
            }
        };

        selectedPlayer.move(
                () ->movePlayersInStartingPosition(onCompleteWithIsAliveCheck),
                pathPointsList.toArray(new Point2D[0])
        );
    }

    @Override
    public void movePlayerVisual(Runnable onComplete, int from, int to) {
        List<Point2D> pathPointsList = new ArrayList<>();

        int start = Math.min(from, to);
        int end = Math.max(from, to);

        int fieldCount = DataProvider.fieldMidPoints.length;

        boolean movingFromStart = false;
        for (int i = start; i <= end; i++) {
            if (i == 0) {
                if (from >= to) {
                    // A player moving towards start will always move to the last startPosition.
                    pathPointsList.add(DataProvider.startPositions[occupiedStartPositions]);
                    occupiedStartPositions++;
                } else {
                    // A player moving from start will always move from the first startPosition.
                    pathPointsList.add(DataProvider.startPositions[0]);
                    occupiedStartPositions--;
                    movingFromStart = true;
                }
            }else if (i < fieldCount) {
                pathPointsList.add(DataProvider.fieldMidPoints[i - 1]);
            } else {
                // Add the first element of endPositions when to is 64
                pathPointsList.add(DataProvider.endPositions[occupiedEndPositions++]);
            }
        }

        // Reverse the list if moving backwards
        if (from > to) {
            Collections.reverse(pathPointsList);
        }

        // necessary because otherwise we might get artifacts when creating and using a new JavaFXGUI
        Runnable onCompleteWithIsAliveCheck = () -> {
            if (isAlive) {
                onComplete.run();
            }
        };

        selectedPlayer.move(
                movingFromStart
                        ? () -> movePlayersInStartingPosition(onCompleteWithIsAliveCheck)
                        : onCompleteWithIsAliveCheck,
                pathPointsList.toArray(new Point2D[0])
        );
    }

    @Override
    public void selectPlayerVisual(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < players.length) {
            if (selectedPlayer != null) {
                selectedPlayer.deselect();
            }

            selectedPlayer = players[playerIndex];
            selectedPlayer.select(PLAYER_SELECTION_COLOR);
        } else {
            LogMessage.error(
                    "Player " + playerIndex + " does not have a visual representation",
                    new IllegalArgumentException()
            );
        }
    }

    @Override
    public void showGameOver(String winnerName, Runnable onComplete) {
        String message = "Alle Spieler sind im Ziel angekommen!\n\nGewonnen hat " + winnerName;
        showAlert(message, onComplete);
    }


    @Override
    public void showIsSuspended(String name, Runnable onComplete) {
        String message = name + " muss leider aussetzen...";
        showAlert(message, onComplete);
    }

    @Override
    public void showReachedEnd(int rank, Runnable onComplete) {
        String message = "Du bist als " + rank + ". im Ziel angekommen!";
        showAlert(message, onComplete);
    }

    @Override
    public void showNoCarrotsBackToStart(String name, Runnable onComplete) {
        String message = name + " hat keine Karotten mehr und muss leider zurück zum Start...";
        showAlert(message, onComplete);
    }

    @Override
    public void showSavingFailed() {
        String message = "Speichern fehlgeschlagen!\nVersuche es mit einem anderen Dateinamen erneut.";
        showAlert(message);
    }

    @Override
    public void showNoValidField(String name, Runnable onComplete) {
        String message = name + " kann auf kein freies Feld ziehen und muss leider aussetzen...";
        showAlert(message, onComplete);
    }

    @Override
    public void showNoCarrotFieldToMoveTo(String name, Runnable onComplete) {
        String message = name + " kann auf kein freies Karotten-Feld ziehen...";
        showAlert(message, onComplete);
    }

    @Override
    public void showNoSaladsToConsume(String name, Runnable onComplete) {
        String message = name + " kann keine weiteren Salate essen und muss leider aussetzen...";
        showAlert(message, onComplete);
    }

    @Override
    public void showAlreadyFirstRank(Runnable onComplete) {
        String message = "Du bist schon an erster Position im Feld!";
        showAlert(message, onComplete);
    }

    @Override
    public void showCantMoveUpRankToEnd(Runnable onComplete) {
        String message = "Du kannst nicht ins Ziel vorrücken, weil Du noch zuviele Salate und/oder Karotten hast!";
        showAlert(message, onComplete);
    }

    @Override
    public void showAlreadyLastRank(Runnable onComplete) {
        String message = "Du bist bereits letzter...";
        showAlert(message, onComplete);
    }

    @Override
    public void showCarrotExchange(int amount, boolean isAddition, Runnable onComplete) {
        String message = "Du " + (isAddition ? "bekommst " : "verlierst ") + amount + " Karotten!";
        showAlert(message, onComplete);
    }

    @Override
    public void showCarrotExchangeDecision(int amount, Runnable onAdd, Runnable onRemove, Runnable onMove, boolean showOnRemove) {
        Runnable[] options = showOnRemove
                ? new Runnable[] { onAdd, onRemove, onMove }
                : new Runnable[] { onAdd, onMove };
        String[] names = showOnRemove
                ? new String[] {
                        String.format("%d nehmen", amount),
                        String.format("%d geben", amount),
                        "weiterziehen"
                }
                : new String[] {
                        String.format("%d nehmen", amount),
                        "weiterziehen"
                };

        String message = "Was möchtest Du tun?";
        gameAlert.showDecisionAlert(options, names, message);
    }

    @Override
    public void showEatingSalad(String playerName, Runnable onComplete) {
        String message = String.format("%s frisst einen Salat...", playerName);
        showAlert(message, onComplete);
    }

    @Override
    public void showToNextCarrotFieldCard(Runnable onComplete) {
        String message = "Ziehe sofort vorwärts zum nächsten Karotten-Feld!";
        showCardAlert(message, onComplete);
    }

    @Override
    public void showToLastCarrotFieldCard(Runnable onComplete) {
        String message = "Ziehe sofort zurück zum letzten Karotten-Feld!";
        showCardAlert(message, onComplete);
    }

    @Override
    public void showTakeTurnAgainCard(Runnable onComplete) {
        String message = "Ziehe gleich noch einmal!";
        showCardAlert(message, onComplete);
    }

    @Override
    public void showConsumeSaladCard(Runnable onComplete) {
        String message = "Friss sofort einen Salat!";
        showCardAlert(message, onComplete);
    }

    @Override
    public void showMoveUpRankCard(Runnable onComplete) {
        String message = "Rücke sofort um eine Position vor!";
        showCardAlert(message, onComplete);
    }

    @Override
    public void showFallBackRankCard(Runnable onComplete) {
        String message = "Du musst sofort um eine Position zurückfallen!";
        showCardAlert(message, onComplete);
    }

    @Override
    public void showGetSuspendedCard(Runnable onComplete) {
        String message = "Du musst einmal aussetzen!";
        showCardAlert(message, onComplete);
    }

    @Override
    public void showFreeLastMoveCard(Runnable onComplete) {
        String message = "Dein letzter Zug kostet nichts!";
        showCardAlert(message, onComplete);
    }

    @Override
    public void showCarrotExchangeCard(int exchangeAmount, Runnable onAdd, Runnable onRemove, Runnable onNothing, boolean showOnRemove) {
        String message = String.format(
                "Du darfst %d Karotten nehmen oder %d Karotten abgeben!",
                exchangeAmount,
                exchangeAmount
        );

        Runnable[] options = showOnRemove
                ? new Runnable[] { onAdd, onRemove, onNothing }
                : new Runnable[] { onAdd, onNothing };

        String[] names = showOnRemove
                ? new String[] {
                        String.format("%d nehmen", exchangeAmount),
                        String.format("%d geben", exchangeAmount),
                        "nichts tun"
                }
                : new String[] {
                        String.format("%d nehmen", exchangeAmount),
                        "nichts tun"
                };

        String decisionMessage = "Was möchtest Du tun?";
        showCardAlert(message, () -> gameAlert.showDecisionAlert(options, names, decisionMessage));
    }

    /**
     * Destroys the GUI and the players on it.
     * It also clears all click handlers and strokes on the fields.
     */
    void destroy() {
        clearClickHandlersAndStrokes();
        for (PlayerVisual player : players) {
            player.destroy();
        }

        isAlive = false;
    }

    /**
     * Shows an alert that can be closed by clicking on the OK button.
     * Its title says "hare card", so it should be used for cards.
     * @param message The message to show.
     * @param onComplete The callback to call when the alert is closed.
     */
    private void showCardAlert(String message, Runnable onComplete) {
            gameAlert.showInformationAlert("Hasenkarte", message, onComplete);
    }

    /**
     * Shows an alert that can be closed by clicking on the OK button.
     * @param message The message to show.
     * @param onComplete The callback to call when the alert is closed.
     */
    private void showAlert(String message, Runnable onComplete) {
            gameAlert.showAlert(ApplicationMain.APPLICATION_NAME, message, onComplete);
    }

    /**
     * Shows an alert that can be closed by clicking on the OK button.
     * @param message The message to show.
     */
    private void showAlert(String message) {
        gameAlert.showAlert(ApplicationMain.APPLICATION_NAME , message, () -> {});
    }

    /**
     * Moves all players that are on a start position to the next start position.
     * @param onComplete The callback to call when all players are moved.
     */
    private void movePlayersInStartingPosition(Runnable onComplete) {
        Runnable runnableChain = onComplete;
        for (int i = occupiedStartPositions; i >= 1; i--) {
            int currentPosition = i;
            int targetPosition = i - 1;

            final Runnable runnable = runnableChain;
            runnableChain = () -> {
                if (occupiedStartPositions >= currentPosition) {
                    for (PlayerVisual player : players) {
                        if (player.getPosition() == DataProvider.startPositions[currentPosition]) {
                            Point2D[] targets = new Point2D[] {
                                    player.getPosition(),
                                    DataProvider.startPositions[targetPosition]
                            };
                            player.move(runnable, targets);
                        }
                    }
                } else {
                    runnable.run();
                }
            };
        }

        runnableChain.run();
    }

    /**
     * Sets up the interaction for a field.
     * @param field The field to set up the interaction for.
     * @param fieldNumber The number of the field.
     * @param carrotsRemaining The number of carrots remaining after moving to the field.
     * @param observer The observer to call when the field is clicked.
     */
    private void setupFieldInteraction(Rectangle field, int fieldNumber, int carrotsRemaining, OnFieldSelected observer) {
        field.setOnMouseClicked(mouseEvent -> {
            clearClickHandlersAndStrokes();
            observer.onFieldSelected(fieldNumber);
        });

        field.setOnMouseEntered(mouseEvent -> {
            if (field != clickableFields[clickableFields.length - 1]) {
                playerCarrots.setText(playerCarrots.getText() + "->" + carrotsRemaining);
                field.setStroke(MOVE_SELECTION_COLOR_VALID);
            }
        });
    }

    /**
     * Resets the appearance (stroke) of a field.
     * @param field The field to reset the appearance of.
     * @param carrots The number of carrots the player has. This is needed to update the playerCarrots label,
     *                because it is changed when the mouse enters a field.
     */
    private void resetFieldAppearance(Rectangle field, int carrots) {
        playerCarrots.setText("Karotten: " + carrots);
        field.setStroke(Color.TRANSPARENT);
    }

    /**
     * Clears all click handlers and strokes on the fields.
     */
    private void clearClickHandlersAndStrokes() {
        for (Rectangle clickable : clickableFields) {
            clickable.setOnMouseClicked(null);
            clickable.setOnMouseEntered(null);
            clickable.setOnMouseExited(null);

            clickable.setStroke(Color.TRANSPARENT);
        }
    }
}
