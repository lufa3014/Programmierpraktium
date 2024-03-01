package logic.Logging;

import logic.ActionCard;
import logic.Tile.TileRegistry;

import java.io.IOException;

/**
 * The `LogMessage` class is a wrapper for the `LoggerCore` class.
 * This class is the only layer that is allowed to interact with the `LoggerCore` class. <br>
 * <br>
 * It provides a simple interface for logging messages.
 * This class is meant to be easily replaceable with a different logging implementation.
 * @see LoggerCore
 *
 * @author Lukas Frahm
 */
public class LogMessage {
    /**
     * Logs the game seed to the log file and overwrites the log file if it already exists.
     * If it does not exist, it is created.
     * @param gameSeed the game seed to be logged
     */
    public static void createLog(long gameSeed) {
        LoggerCore.createLog(gameSeed);
    }

    /**
     * Logs the end of the log file, if something got logged before.
     */
    public static void endLog() {
        if (LoggerCore.isLogging()) {
            LoggerCore.log("\n+-----------------+\n| End of log file |\n+-----------------+");
        }
    }

    /**
     * Logs that the game is over.
     */
    public static void gameOver() {
        LoggerCore.log("\n * Game over * \n");
    }

    /**
     * Logs the winner of the game.
     * @param playerName the name of the player who won
     * @param rank the rank of the player who won
     */
    public static void isRanked(String playerName, int rank) {
        LoggerCore.log(" ! " + playerName + " is ranked " + rank + ". player");
    }

    /**
     * Logs that the game is a draw.
     * @param playerName the name of the player whose turn it is
     */
    public static void startTurn(String playerName) {
        LoggerCore.log("Start turn of: " + playerName);
    }

    /**
     * Logs that the turn of a player has ended.
     * @param playerName the name of the player whose turn ended
     */
    public static void endTurn(String playerName) {
        LoggerCore.log("End turn of: " + playerName);
    }

    /**
     * Logs that a player has reached the end.
     * @param playerName the name of the player who reached the end
     * @param rank the rank of the player who reached the end
     */
    public static void reachesEnd(String playerName, int rank) {
        LoggerCore.log(" ! " + playerName + " reaches the end as " + rank + ". player");
    }

    /**
     * Logs that a player got suspended.
     * @param playerName the name of the player who got suspended
     */
    public static void getsSuspended(String playerName) {
        LoggerCore.log(" - " + playerName + " gets suspended");
    }

    /**
     * Logs that a player is no longer suspended.
     * @param playerName the name of the player who is no longer suspended
     */
    public static void getsUnsuspended(String playerName) {
        LoggerCore.log(" - " + playerName + " is no longer suspended");
    }

    /**
     * Logs that a player is eating a salad or is no longer eating a salad.
     * @param playerName the name of the player who is eating a salad
     * @param isEatingSalad whether the player is eating a salad or not
     */
    public static void isEatingSalad(String playerName, boolean isEatingSalad) {
        LoggerCore.log(" - " + playerName + " is " + (isEatingSalad ? "" : "no longer ") + "eating a salad");
    }

    /**
     * Logs that a player has to skip their turn.
     * @param playerName the name of the player who has to skip their turn
     */
    public static void getsSkipped(String playerName) {
        LoggerCore.log(" - " + playerName + " has to skip this turn");
    }

    /**
     * Logs what card a player draws.
     * @param playerName the name of the player who draws the card
     * @param card the card that the player draws
     */
    public static void drawsCard(String playerName, ActionCard card) {
        LoggerCore.log(" - " + playerName + " draws " + card);
    }

    /**
     * Logs that the player chose to add carrots.
     * @param playerName the name of the player who chose to add carrots
     * @param amount the amount of carrots the player chose to add
     */
    public static void choosesToAddCarrots(String playerName, int amount) {
        LoggerCore.log(" * " + playerName + " chooses to add " + amount + " carrots");
    }

    /**
     * Logs that the player chose to remove carrots.
     * @param playerName the name of the player who chose to remove carrots
     * @param amount the amount of carrots the player chose to remove
     */
    public static void choosesToRemoveCarrots(String playerName, int amount) {
        LoggerCore.log(" * " + playerName + " chooses to remove " + amount + " carrots");
    }

    /**
     * Logs that the player chose to move.
     * @param playerName the name of the player who chose to move
     */
    public static void choosesToMove(String playerName) {
        LoggerCore.log(" * " + playerName + " chooses to move");
    }

    /**
     * Logs that the player chose to do nothing.
     * @param playerName the name of the player who chose to do nothing
     */
    public static void choosesToDoNothing(String playerName) {
        LoggerCore.log(" * " + playerName + " chooses to do nothing");
    }

    /**
     * Logs that the player has no field to move to.
     * @param playerName the name of the player who has no field to move to
     */
    public static void hasNoFieldToMoveTo(String playerName) {
        LoggerCore.log(" - " + playerName + " has no field to move to");
    }

    /**
     * Logs that the player is already in first place.
     * @param playerName the name of the player who is already in first place
     */
    public static void isAlreadyFirstRank(String playerName) {
        LoggerCore.log(" - " + playerName + " is already in first place");
    }

    /**
     * Logs that the player is already in last place.
     * @param playerName the name of the player who is already in last place
     */
    public static void isAlreadyLastRank(String playerName) {
        LoggerCore.log(" - " + playerName + " is already in last place");
    }

    /**
     * Logs that the player consumed a salad.
     * @param playerName the name of the player who consumed a salad
     */
    public static void consumesSalad(String playerName) {
        LoggerCore.log(" * " + playerName + " consumes a salad");
    }

    /**
     * Logs that the player has no salads to consume.
     * @param playerName the name of the player who has no salads to consume
     */
    public static void hasNoSaladsToConsume(String playerName) {
        LoggerCore.log(" - " + playerName + " has no salads to consume");
    }

    /**
     * Logs that the player moved from one field to another and what type of fields they were.
     * @param playerName the name of the player who moved
     * @param from the field the player moved from
     * @param fromTile the type of the field the player moved from
     * @param to the field the player moved to
     * @param toTile the type of the field the player moved to
     */
    public static void moves(String playerName, int from, TileRegistry fromTile, int to, TileRegistry toTile) {
        LoggerCore.log(" * " + playerName + " moves from " + from + "(" + fromTile + ") to " + to + "(" + toTile + ")");
    }

    /**
     * Logs that the player has to go back to start.
     * @param playerName the name of the player who has to go back to start
     */
    public static void movesBackToStart(String playerName) {
        LoggerCore.log(" * " + playerName + "has to go back to start");
    }

    /**
     * Logs an error message.
     * @param message the error message
     * @param e the exception that caused the error
     */
    public static void error(String message, Exception e) {
        String fullMessage = "Error: " + message + "\n" + e.getMessage();
        String[] lines = fullMessage.split("\n");

        // Find the length of the longest line
        int maxLength = 0;
        for (String line : lines) {
            if (line.length() > maxLength) {
                maxLength = line.length();
            }
        }

        String boxTopAndBottom = "+" + "-".repeat(maxLength + 2) + "+";
        StringBuilder boxedError = new StringBuilder(boxTopAndBottom + "\n");
        for (String line : lines) {
            boxedError.append("| ").append(line);

            // Pad the end of the line if it's shorter than the longest line
            int paddingLength = maxLength - line.length();
            boxedError.append(" ".repeat(paddingLength)).append(" |\n");
        }
        boxedError.append(boxTopAndBottom);

        if (LoggerCore.isLogging()) {
            LoggerCore.log(boxedError.toString());
        } else {
            System.err.println(boxedError);
        }
    }
}
