package logic.Logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The `LoggerCore` class is responsible for logging to a file.
 * This is the core class of the logging feature and is the only layer that is allowed to interact with the files.
 * @see LogMessage
 *
 * @author Lukas Frahm
 */
class LoggerCore {

    /**
     * The name of the log file.
     */
    private static final String LOG_FILE_NAME = "log.txt";

    /**
     * Logs a message to the log file. The log file is created if it does not exist.
     * If logging fails, the error is printed to the standard error stream but the program continues.
     * @param message the message to be logged
     */
    static void log(String message) {
        try {
            String jarPath = getJarPath();
            File logFile = new File(jarPath, LOG_FILE_NAME);
            createLogFileIfNeeded(logFile);
            appendMessageToFile(message, logFile);

        // If an error occurs, we only print it to the standard error stream but continue the program.
        // This is because the logging feature is not essential for the game to work.
        } catch (URISyntaxException e) {
            System.err.println("Error determining the directory path of the .jar file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error writing to the log file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }
    }

    /**
     * Creates a new log file and logs the game seed to it.
     * @param gameSeed the game seed to be logged
     */
    static void createLog(long gameSeed) {
        try {
            String jarPath = getJarPath();
            File logFile = new File(jarPath, "log.txt");

            if (logFile.exists()) {
                boolean isDeleted = logFile.delete();
                if (!isDeleted) {
                    throw new IOException("log file could not be deleted");
                }
            }

            createLogFileIfNeeded(logFile);
            logWithTimestampAndGameSeed(gameSeed);
        } catch (URISyntaxException e) {
            System.err.println("Error determining the directory path of the .jar file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error resetting the log file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }
    }

    /**
     * Returns the path of the directory the .jar file is in.
     * @return the path of the directory the .jar file is in
     * @throws URISyntaxException if the path of the .jar file cannot be determined
     */
    private static String getJarPath() throws URISyntaxException {
        return new File(LoggerCore.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
        ).getParent();
    }

    /**
     * Creates a new log file if it does not exist.
     * @param logFile the log file to be created
     * @throws IOException if the log file could not be created
     */
    private static void createLogFileIfNeeded(File logFile) throws IOException {
        if (!logFile.exists()) {
            boolean isCreated = logFile.createNewFile();
            if (!isCreated) {
                throw new IOException("log file could not be created");
            }
        }
    }

    /**
     * Appends a message to the log file.
     * @param message the message to be appended
     * @param logFile the log file to append the message to
     * @throws IOException if the message could not be appended to the log file
     */
    private static void appendMessageToFile(String message, File logFile) throws IOException {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(message + System.lineSeparator());
        }
    }

    /**
     * Logs a message to the log file containing a timestamp and the game seed.
     * @param gameSeed the game seed to be logged
     */
    private static void logWithTimestampAndGameSeed(long gameSeed) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy ss:mm:HH");
        String timestamp = now.format(formatter);

        String logMessage = String.format("Timestamp: %s - GameSeed: %d", timestamp, gameSeed);
        String boxTopAndBottom = "+" + "-".repeat(logMessage.length() + 2) + "+";
        String boxedMessage = boxTopAndBottom + "\n| " + logMessage + " |\n" + boxTopAndBottom;

        log(boxedMessage);
    }

    /**
     * Checks if the logging feature is enabled.
     * @return {@code true} if the logging feature is enabled, {@code false} otherwise
     */
    static boolean isLogging() {
        boolean isLogging = false;

        try {
            String jarPath = getJarPath();
            File logFile = new File(jarPath, "log.txt");
            isLogging = logFile.exists();
        } catch (URISyntaxException e) {
            System.err.println("Error determining the directory path of the .jar file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unknown error: " + e.getMessage());
        }

        return isLogging;
    }
}
