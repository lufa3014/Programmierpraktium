package gui;

/**
 * Wrapper class is necessary as the main class for our program must not inherit
 * from {@link javafx.application.Application}
 *
 * @author mjo
 */
public class JarMain {

    public static void main(String... args) {
        ApplicationMain.main(args);
    }
}
