package logic;

/**
 * The `OnFieldSelected` interface represents a callback that is called when a field is selected.
 * @see GUIConnector#enableMoveSelection(int[], int, int[], OnFieldSelected)
 *
 * @author Lukas Frahm
 */
@FunctionalInterface
public interface OnFieldSelected {

    /**
     * Called when a field is selected (hopefully).
     * @param field The selected field.
     */
    void onFieldSelected(int field);
}
