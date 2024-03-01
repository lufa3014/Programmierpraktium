package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import logic.Game;
import logic.GameData;
import logic.Logging.LogMessage;

/**
 * The controller class for the user interface. This class is responsible for the communication between the user
 * interface and the game logic. It also handles the creation of the game and the gui.
 *
 * @author Lukas Frahm
 */
public class UserInterfaceController implements Initializable {

    // ==========================================================
    // FXML Fields
    // ==========================================================

    @FXML private VBox root;

    @FXML private AnchorPane boardAnchorPane;
    @FXML private ImageView boardImageView;

    @FXML private StackPane controlsStackPane;

    @FXML private Spinner<Integer> playerCountSpinner;

    @FXML private VBox playerNameVBox;

    @FXML private Button newButton;
    @FXML private Button loadButton;
    @FXML private Button saveButton;
    @FXML private Button exitButton;

    @FXML private VBox playerInfoVBox;
    @FXML private Label nameLabel;
    @FXML private Label carrotsLabel;
    @FXML private Label saladsLabel;
    @FXML private Label playerCountLabel;
    @FXML private Label playerNamesLabel;

    // ==========================================================
    // Constants
    // ==========================================================

    /**
     * The default player names that are used when the user does not enter any names.
     */
    private static final String[] DEFAULT_PLAYER_NAMES = {
            "Anton",
            "Berta",
            "Cesar",
            "Doris",
            "Emile",
            "Frank",
    };

    /**
     * The size of a field. This should roughly be the same as the size of the fields in the image.
     */
    private static final double FIELD_SIZE = 59;

    /**
     * The width of the border of a field.
     */
    private static final double FIELD_BORDER_WIDTH = 2.8;

    /**
     * The arc size of a field. This should roughly be the same as the arc size of the fields in the image.
     */
    private static final double FIELD_ARC_SIZE = 15;

    /**
     * The size of a player.
     */
    private static final double PLAYER_SIZE = 56;

    /**
     * The width of the border of a selected player.
     */
    private static final double PLAYER_SELECTION_BORDER_WIDTH = 2.8;

    // ==========================================================
    // Instance Variables
    // ==========================================================

    /**
     * The text fields for the player names.
     */
    private final List<TextField> playerNameFields = new ArrayList<>();

    /**
     * The gui that is currently running.
     */
    private JavaFXGUI gui;

    /**
     * The clickable fields of the board.
     */
    private Rectangle[] clickableFields;

    /**
     * The game that is currently running.
     */
    private Game game;

    /**
     * The blueprint used for alerts.
     */
    private GameAlert gameAlert;

    /**
     * Whether a game is currently running.
     * This is used to determine whether the {@link #newButton} button should be a "New" button or a "Start" Button.
     */
    private boolean isGameStarted = false;

    private File latestSaveGame;

    // ==========================================================
    // Methods
    // ==========================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtons();
        setupSpinner();

        boardImageView.fitWidthProperty().bind(boardAnchorPane.widthProperty());
        boardImageView.fitHeightProperty().bind(boardAnchorPane.heightProperty());
        boardImageView.setManaged(false);

        HBox.setHgrow(boardAnchorPane, Priority.ALWAYS);
        HBox.setHgrow(controlsStackPane, Priority.NEVER);

        // FXML fields are usually not initialized before initialize() is called.
        // Used to ensure that the board is loaded before creating the clickable fields.
        Platform.runLater(() ->  {
            clickableFields = new RectangleFactory(
                    boardImageView.getImage().getWidth(),
                    boardImageView.getImage().getHeight(),
                    boardImageView.fitWidthProperty(),
                    boardImageView.fitHeightProperty(),
                    boardAnchorPane
            ).createRectangles(DataProvider.fieldMidPoints, FIELD_SIZE, FIELD_BORDER_WIDTH, FIELD_ARC_SIZE);

            gameAlert = new GameAlert((Stage) root.getScene().getWindow(), boardAnchorPane);

            showMenu(true);
        });
    }

    /**
     * Sets up the buttons:
     * {@link #newButton}, {@link #loadButton}, {@link #saveButton}, {@link #exitButton}.
     */
    private void setupButtons() {
        newButton.setOnMouseClicked(event -> {
            if (isGameStarted) {
                onNewButtonPressed();
            } else {
                onStartButtonPressed();
            }
        });

        // We could do this in the FXML instead, but this is more readable and much cleaner.
        loadButton.setOnAction(event -> onLoadButtonPressed());
        saveButton.setOnAction(event -> onSaveButtonPressed());
        exitButton.setOnAction(event -> onExitButtonPressed());
    }

    /**
     * Called when the save button is pressed.
     * If the game fails to save, an alert is shown and the game continues normally.
     */
    private void onSaveButtonPressed() {
        FileChooser fileChooser = createFileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON files", "*.json"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        fileChooser.setTitle("Speichere JSON Datei");

        File file = fileChooser.showSaveDialog(root.getScene().getWindow());
        if (file != null) {
            game.save(file);
            latestSaveGame = file;
        }
    }

    /**
     * Called when the load button is pressed.
     * If the game fails to load, an alert is shown and the game continues normally (if there is a game running).
     */
    private void onLoadButtonPressed() {
        FileChooser fileChooser = createFileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON files", "*.json"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        fileChooser.setTitle("Lade JSON-Datei");

        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            GameData saveGame = GameData.getGameDataFromSaveFile(file);

            if (saveGame != null) {
                // we don't check if the last game was saved and ask the user if he wants to save it,
                // every time the user loads a game, because this would annoy the user more
                // than it would help him.
                startGame(saveGame);
            } else {
                String message = "Laden fehlgeschlagen!\n" +
                        "Stelle sicher, dass die Datei auch ein gültiger Spielstand ist.";

                gameAlert.showAlert(ApplicationMain.APPLICATION_NAME, message, () -> {});
            }
        }
    }

    /**
     * Sets up the {@link #playerCountSpinner} and the {@link #playerNameFields}.
     */
    private void setupSpinner() {
        playerCountSpinner.valueProperty().addListener(
                (observable, oldValue, newValue) -> updatePlayerNameFields(newValue)
        );

        updatePlayerNameFields(playerCountSpinner.getValue());
    }

    /**
     * Updates the {@link #playerNameFields} to match the given player count.
     * @param playerCount The new player count.
     * @see #playerNameFields
     * @see #setupSpinner()
     */
    private void updatePlayerNameFields(Integer playerCount) {
        int currentFieldsCount = playerNameVBox.getChildren().size();

        if (playerCount < currentFieldsCount) {
            playerNameVBox.getChildren().remove(playerCount, currentFieldsCount);
            playerNameFields.subList(playerCount, playerNameFields.size()).clear();
        }

        for (int i = currentFieldsCount; i < playerCount; i++) {
            TextField textField = new TextField();
            textField.setPromptText(DEFAULT_PLAYER_NAMES[i]);
            textField.getStyleClass().add("player-name-field");
            playerNameVBox.getChildren().add(textField);
            playerNameFields.add(textField);
        }
    }

    /**
     * Called when the exit button is pressed.
     * If there is a game running, the log is ended before exiting.
     */
    private void onExitButtonPressed() {
        String message = "Willst du wirklich beenden?";

        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Runnable> options = new ArrayList<>();

        if (game != null) {
            LogMessage.endLog();

            if (latestSaveGame == null || !game.representsCurrentGame(latestSaveGame)) {
                if (latestSaveGame == null) {
                    message += "\n\nDas Spiel wurde noch nicht gespeichert!";
                } else {
                    message += "\n\nDas Spiel wurde seit dem letzten Speichern verändert!";
                }

                labels.add("Speichern und beenden");
                options.add(() -> {
                    onSaveButtonPressed();

                    // if the user cancels the save dialog or saving fails, the game should not exit
                    if (game.representsCurrentGame(latestSaveGame)) {
                        Platform.exit();
                    }
                });
            }
        }

        labels.add("Beenden");
        options.add(Platform::exit);

        labels.add("Abbrechen");
        options.add(() -> {
        });

        gameAlert.showDecisionAlert(options.toArray(new Runnable[0]), labels.toArray(new String[0]), message);
    }

    /**
     * Called when the new button is pressed.
     * If there is a game running, the game is ended before starting a new game.
     * This works by destroying the current gui, so that the callback-system is interrupted.
     */
    private void onNewButtonPressed() {
        Runnable onNewGame = () -> {
            if (gui != null) {
                gui.destroy();
                gui = null;
            }

            showMenu(true);

            isGameStarted = false;
        };

        String message = "Willst du wirklich ein neues Spiel starten?";

        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Runnable> options = new ArrayList<>();

        if (game != null) {
            LogMessage.endLog();

            if (latestSaveGame == null || !game.representsCurrentGame(latestSaveGame)) {
                if (latestSaveGame == null) {
                    message += "\n\nDas Spiel wurde noch nicht gespeichert!";
                } else {
                    message += "\n\nDas Spiel wurde seit dem letzten Speichern verändert!";
                }

                labels.add("Speichern und Spiel erstellen");
                options.add(() -> {
                    onSaveButtonPressed();

                    // if the user cancels the save dialog or saving fails, the game should not exit
                    if (game.representsCurrentGame(latestSaveGame)) {
                        onNewGame.run();
                    }
                });
            }
        }

        labels.add("Spiel erstellen");
        options.add(onNewGame);

        labels.add("Abbrechen");
        options.add(() -> {
        });

        gameAlert.showDecisionAlert(options.toArray(new Runnable[0]), labels.toArray(new String[0]), message);
    }

    /**
     * Called when the start button is pressed.
     * This starts a new game.
     * @see #startGame()
     */
    private void onStartButtonPressed() {
        startGame();
    }

    /**
     * Shows or hides parts of the menu.
     * @param isStart Whether the menu should be in start mode or not.
     */
    private void showMenu(boolean isStart) {
        saveButton.setDisable(isStart);

        newButton.setText(isStart ? "Start" : "Neu");

        playerInfoVBox.setVisible(!isStart);

        playerCountLabel.setVisible(isStart);
        playerCountSpinner.setVisible(isStart);
        playerNamesLabel.setVisible(isStart);
        playerNameVBox.setVisible(isStart);

        ColorAdjust colorAdjust = null;
        if (isStart) {
            colorAdjust = new ColorAdjust();
            colorAdjust.setSaturation(-1);
            colorAdjust.setBrightness(-.5);
        }
        boardImageView.setEffect(colorAdjust);
    }

    /**
     * Creates a new file chooser with the current directory as the initial directory.
     * @return A new file chooser with the current directory as the initial directory.
     */
    private FileChooser createFileChooser() {
        //https://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
        File currDir = null;
        try {
            currDir = new File(Game.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ex) {
            LogMessage.error("Error while getting current directory", ex);
        }

        FileChooser fileChooser = new FileChooser();
        if (currDir != null) {

            fileChooser.setInitialDirectory(currDir.getParentFile());
        }

        return fileChooser;
    }

    /**
     * Starts a new game with the given player names.
     * @see #getPlayerNames()
     */
    private void startGame() {
        if (game != null) {
            // not necessary because log gets overwritten anyway,
            // but if in future versions a new log file is created,
            // this would be necessary
            LogMessage.endLog();
        }

        showMenu(false);

        String[] playerNames = getPlayerNames();
        createNewGUI(playerNames.length);
        game = new Game(playerNames, gui);
        game.start();

        isGameStarted = true;
    }

    /**
     * Starts a new game with the given save game.
     * @param saveGame The save game to start.
     */
    private void startGame(GameData saveGame) {
        if (game != null) {
            // not necessary because log gets overwritten anyway,
            // but if in future versions a new log file is created,
            // this would be necessary
            LogMessage.endLog();
        }

        showMenu(false);

        createNewGUI(saveGame.getPlayerCount());
        game = new Game(saveGame, gui);
        game.start();

        isGameStarted = true;
    }

    /**
     * Creates a new gui and player visuals with the given player count.
     * @param playerCount The player count of the new gui.
     */
    private void createNewGUI(int playerCount) {
        if (gui != null) {
            gui.destroy();
            gui = null;
        }

        PlayerVisual[] players = new PlayerVisual[playerCount];
        for (int i = 0; i < playerCount; i++) {
            players[i] = new PlayerVisual(
                    i,
                    PLAYER_SIZE,
                    PLAYER_SELECTION_BORDER_WIDTH,
                    boardImageView,
                    boardAnchorPane
            );
        }

        gui = new JavaFXGUI(clickableFields, players, nameLabel, carrotsLabel, saladsLabel, gameAlert);
    }

    /**
     * Gets the player names from the {@link #playerNameFields}.
     * If a field is empty, the prompt text is used instead, so that {@link #DEFAULT_PLAYER_NAMES} is used.
     * @return The player names from the {@link #playerNameFields}.
     */
    private String[] getPlayerNames() {
        String[] playerNames = new String[playerNameFields.size()];
        for (int i = 0; i < playerNames.length; i++) {
            TextField textField = playerNameFields.get(i);
            String text = textField.getText();
            playerNames[i] = text.isEmpty() ? textField.getPromptText() : text;
        }

        return playerNames;
    }
}
