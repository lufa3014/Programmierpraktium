package logic;

/**
 * The `Action` interface represents an action that can be executed by a player.
 * @see ActionBuilder
 *
 * @author Lukas Frahm
 */
@FunctionalInterface
public interface Action {

    /**
     * Execute the action.
    * @param gui The GUI connector used in the action.
    * @param context The game context to use for executing the action.
    * @param player The player to execute the action for.
    * @param onComplete A callback to call when the action is completed.
    */
    void execute(GUIConnector gui, GameContext context, Player player, Runnable onComplete);
}
