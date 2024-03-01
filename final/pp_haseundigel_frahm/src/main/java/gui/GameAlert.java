package gui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import logic.Logging.LogMessage;

/**
 * This class is used to display alerts in the game.
 * It contains the different types of alerts that can be displayed in the game.
 * @author Lukas Frahm
 */
public class GameAlert {

    /**
     * Determines the space between the buttons in the decision alert.
     */
    private static final int SPACE_BETWEEN_BUTTONS = 10;

    /**
     * The owner (Window) of the alert.
     */
    private final Stage owner;

    /**
     * The node the alert is displayed on.
     * This is used to calculate the position of the alert.
     */
    private final Node targetNode;

    /**
     * Create a new GameAlert.
     * @param owner The owner (Window) of the alert.
     * @param targetNode The node the alert is displayed on.
     */
    GameAlert(Stage owner, Node targetNode) {
        this.owner = owner;
        this.targetNode = targetNode;
    }

    /**
     * Show an alert with a title, a message and a button.
     * @param title The title of the alert.
     * @param message The message of the alert.
     * @param onComplete The action to be executed when the alert is closed. (Button is pressed)
     */
    void showAlert(@SuppressWarnings("SameParameterValue") String title, String message, Runnable onComplete) {
        Alert alert = createAlertDialog(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getButtonTypes().add(ButtonType.OK);

        Platform.runLater(() -> {
            alert.showAndWait();
            onComplete.run();
        });
    }

    /**
     * Show an alert with a title, a message and a button.
     * This alert is marked as an information alert and has a different design than the normal alert.
     * @see #showAlert(String, String, Runnable)
     * @param title The title of the alert.
     * @param message The message of the alert.
     * @param onComplete The action to be executed when the alert is closed. (Button is pressed)
     */
    void showInformationAlert(@SuppressWarnings("SameParameterValue") String title, String message, Runnable onComplete) {
        Alert alert = createAlertDialog(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.getButtonTypes().add(ButtonType.OK);

        Platform.runLater(() -> {
            alert.showAndWait();
            onComplete.run();
        });
    }

    /**
     * Show an alert with a title, a message and three different buttons.
     * @param options The actions to be executed when the alert is closed.
     * @param optionNames The names of the buttons.
     *                    The length of this array must be the same as the length of the options array.
     *                    The order of the names must match the order of the options.
     * @param message The message of the alert.
     */
    void showDecisionAlert(Runnable[] options, String[] optionNames, String message) {
        if (options.length == optionNames.length) {
            Alert gameAlert = createAlertDialog(Alert.AlertType.NONE);
            gameAlert.setHeaderText(message);

            HBox buttonBox = new HBox();
            buttonBox.setSpacing(SPACE_BETWEEN_BUTTONS);

            for (int i = 0; i < options.length; i++) {
                Button button = new Button(optionNames[i]);

                final int index = i;
                button.setOnAction(e -> {
                    ((Stage) gameAlert.getDialogPane().getScene().getWindow()).close();
                    options[index].run();
                });

                button.setFocusTraversable(false);
                buttonBox.getChildren().add(button);
            }

            Platform.runLater(() -> {
                gameAlert.getDialogPane().setContent(buttonBox);
                gameAlert.showAndWait();
            });

        } else {
            LogMessage.error("options and optionNames must have the same length", new IllegalArgumentException());
        }
    }

    /**
     * Create a new alert.
     * This method is used as a helper to create the different types of alerts.
     * @param type The type of the alert.
     * @return The created alert.
     */
    private Alert createAlertDialog(Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.initOwner(owner);
        alert.getButtonTypes().clear();

        alert.showingProperty().addListener((observable, oldValue, showing) -> {
            if (showing) {
                Bounds boundsInScene = targetNode.localToScene(targetNode.getBoundsInLocal());
                double centerXPosition = owner.getX() + boundsInScene.getMinX() +
                        boundsInScene.getWidth() / 2 - alert.getWidth() / 2;
                double centerYPosition = owner.getY() + boundsInScene.getMinY() +
                        boundsInScene.getHeight() / 2 - alert.getHeight() / 2;

                alert.setX(centerXPosition);
                alert.setY(centerYPosition);
            }
        });

        return alert;
    }
}