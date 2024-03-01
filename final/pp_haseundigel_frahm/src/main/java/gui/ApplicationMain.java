package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that starts our application.
 *
 * @author mjo, Lukas Frahm
 */
public class ApplicationMain extends Application {
    /**
     * The name of the application
     */
    static final String APPLICATION_NAME = "Hase und Igel";

    /**
     * The height of the toolbar.<br>
     * This is used to calculate the minimum window heights
     */
    private static final int TOOLBAR_HEIGHT = 37;

    /**
     * the minimum window width.
     */
    private static final int MINIMUM_WINDOW_WIDTH = 1125;

    /**
     * the minimum window height (including toolbar).
     */
    private static final int MINIMUM_WINDOW_HEIGHT = 755 + TOOLBAR_HEIGHT;

    /**
     * Creating the stage and showing it. This is where the initial size and the
     * title of the window are set.
     *
     * @param stage the stage to be shown
     * @throws IOException e
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationMain.class.getResource("/gui/fx/UserInterface.fxml"));
        Scene scene = new Scene(fxmlLoader.load());


        try {
            Image applicationIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/gui/img/icon.png")));
            stage.getIcons().add(applicationIcon);
        } catch (Exception ignored) {}


        stage.setTitle(APPLICATION_NAME);
        stage.setScene(scene);
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);

        stage.show();
    }

    /**
     * Main method
     *
     * @param args unused
     */
    public static void main(String... args) {
        launch(args);
    }
}
