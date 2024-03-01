package gui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * The `PlayerVisual` class represents the visual representation of a player.
 *
 * @author Lukas Frahm
 */
class PlayerVisual {
    /**
     * The `PlayerColor` enum represents the different player colors.
     */
    private enum PlayerColor {
        BLUE(Color.rgb(25, 97, 156)),    // #19619C
        YELLOW(Color.rgb(222, 205, 27)), // #DECD1B
        GREEN(Color.rgb(81, 159, 23)),   // #519F17
        ORANGE(Color.rgb(223, 89, 34)),  // #DF5922
        RED(Color.DARKRED),              // Not using the actual player color, because red on red is hard to see
        WHITE(Color.rgb(172, 158, 121)); // #AC9E79

        /**
         * The color value of the player color.
         */
        private final Color color;

        /**
         * Create a new player color.
         * @param color The color value of the player color.
         */
        PlayerColor(Color color) {
            this.color = color;
        }

        /**
         * Get the player color by its index.
         * @param index The index of the player color.
         * @return The player color. If the index is out of bounds, the white player color is returned.
         */
        static PlayerColor getColorByIndex(int index) {
            if (index >= 0 && index < PlayerColor.values().length) {
                return PlayerColor.values()[index];
            } else {
                return PlayerColor.WHITE;
            }
        }

        /**
         * Just a normal getter. ¯\_(ツ)_/¯
         * @return The color value of the player color.
         */
        Color getValue() {
            return color;
        }
    }

    /**
     * The Image objects for the different player colors.
     * They are loaded from the resources' folder.
     */
    private static final Image
            BlueImage = DataProvider.loadImageFromResources("/gui/img/PlayerBlue.png"),
            YellowImage = DataProvider.loadImageFromResources("/gui/img/PlayerYellow.png"),
            GreenImage = DataProvider.loadImageFromResources("/gui/img/PlayerGreen.png"),
            OrangeImage = DataProvider.loadImageFromResources("/gui/img/PlayerOrange.png"),
            RedImage = DataProvider.loadImageFromResources("/gui/img/PlayerRed.png"),
            WhiteImage = DataProvider.loadImageFromResources("/gui/img/PlayerWhite.png");

    /**
     * The Image objects for the different player colors.
     */
    private static final Image[] images = { BlueImage, YellowImage, GreenImage, OrangeImage, RedImage, WhiteImage };

    /**
     * The speed of the moving animation.
     * The higher the value, the faster the animation.
     */
    private static final double MOVING_ANIMATION_SPEED = 150;

    /**
     * The frame rate of the moving animation.
     * The higher the value, the smoother the animation.
     * Don't set this value too high... It will cause the game to lag.
     */
    private static final int MOVING_ANIMATION_FRAME_RATE = 60;

    /**
     * The visual representation of the player as a rectangle.
     */
    private final Rectangle visual;

    /**
     * The color of the player.
     */
    private final PlayerColor color;

    /**
     * The size of the player.
     */
    private final double size;

    /**
     * The width of the board.
     */
    private final double boardWidth;

    /**
     * The height of the board.
     */
    private final double boardHeight;

    /**
     * The fit width of the board, used to bind the player's position to the board.
     */
    private final DoubleProperty boardFitWidth;

    /**
     * The fit height of the board, used to bind the player's position to the board.
     */
    private final DoubleProperty boardFitHeight;

    /**
     * The current position of the player.
     */
    private Point2D position;

    /**
     * Create a new player visual.
     * @param index The index of the player, used to determine the player color.
     * @param size The size of the player.
     * @param selectionBorderWidth The width of the selection border.
     * @param board The board image.
     * @param canvas The canvas to draw on.
     */
    PlayerVisual(int index, double size, double selectionBorderWidth, ImageView board, Pane canvas) {
        color = PlayerColor.getColorByIndex(index);
        this.size = size;
        boardWidth = board.getImage().getWidth();
        boardHeight = board.getImage().getHeight();
        boardFitWidth = board.fitWidthProperty();
        boardFitHeight = board.fitHeightProperty();

        visual = new RectangleFactory(
                boardWidth,
                boardHeight,
                boardFitWidth,
                boardFitHeight,
                canvas
        ).createRectangle(Point2D.ZERO, size, selectionBorderWidth, Integer.MAX_VALUE);

        position = new Point2D(0, 0);
        relocate(position);

        if (images[index] != null) {
            visual.setFill(new ImagePattern(images[index]));
        } else {
            // If the image is not available, use the color instead
            // Better than crashing... ¯\_(ツ)_/¯
            visual.setFill(color.getValue());
        }

        canvas.getChildren().add(visual);
    }

    /**
     * Relocate the player to the given position.<br>
     * <b>Never use any other method to relocate the player!</b>
     * @param target The target position.
     */
    void relocate(Point2D target) {
        position = target;

        double relativeWidth = size / boardWidth;
        double relativeHeight = size / boardHeight;

        final Rectangle visual = this.visual;
        if (visual != null) {
            visual.xProperty().bind(boardFitWidth.multiply((target.getX() / boardWidth) - relativeWidth / 2));
            visual.yProperty().bind(boardFitHeight.multiply((target.getY() / boardHeight) - relativeHeight / 2));
        }
    }

    /**
     * Move the player to the given target position.
     * @param onComplete The callback to call when the animation is finished.
     * @param targets The target positions. The player will move from the first position to the second, etc.
     *                The player will teleport to a position if the targets array only contains one position.
     */
    void move(Runnable onComplete, Point2D[] targets) {
        if (targets.length == 1) {
            relocate(targets[0]);
            onComplete.run();
            return;
        }

        // Create a sequence to store alle the animations (timelines)
        SequentialTransition sequentialTransition = new SequentialTransition();

        // Create a timeline for each segment of the path (from one target to the next)
        for (int i = 1; i < targets.length; i++) {

            double distance = targets[i].distance(targets[i - 1]);
            double durationInSeconds = distance / (MOVING_ANIMATION_SPEED * Math.pow(i, .5));

            // Create a pair for the current segment
            Point2D[] currentTargetPair = new Point2D[] { targets[i - 1], targets[i] };
            Duration currentDuration = Duration.seconds(durationInSeconds);

            Timeline timeline = new Timeline();

            // Use the pair to create a timeline for the current segment
            for (int segmentIndex = 0; segmentIndex < currentTargetPair.length - 1; segmentIndex++) {
                double startX = currentTargetPair[segmentIndex].getX();
                double startY = currentTargetPair[segmentIndex].getY();
                double endX = currentTargetPair[segmentIndex + 1].getX();
                double endY = currentTargetPair[segmentIndex + 1].getY();

                // Create a keyframe for the start position
                KeyFrame startKeyFrame = new KeyFrame(Duration.seconds(
                        segmentIndex * currentDuration.toSeconds()),
                        e -> relocate(new Point2D(startX, startY))
                );
                timeline.getKeyFrames().add(startKeyFrame);

                // Create some intermediate keyframes to make the animation smoother
                for (int fractionIndex = 1; fractionIndex <= MOVING_ANIMATION_FRAME_RATE; fractionIndex++) {
                    double fraction = (double)fractionIndex / MOVING_ANIMATION_FRAME_RATE;

                    // Find the positions for intermediate keyframes by interpolating
                    double x = startX + (endX - startX) * fraction;
                    double y = startY + (endY - startY) * fraction;

                    // Create a keyframe for the intermediate position
                    KeyFrame intermediateKeyFrame = new KeyFrame(Duration.seconds(
                            segmentIndex * currentDuration.toSeconds() + fraction * currentDuration.toSeconds()),
                            e -> relocate(new Point2D(x, y))
                    );
                    timeline.getKeyFrames().add(intermediateKeyFrame);
                }
            }

            // Add the timeline to the sequence
            sequentialTransition.getChildren().add(timeline);
        }

        // safely store the visual in a local variable
        // this is necessary because the visual could be destroyed while the animation is running
        final Rectangle visual = this.visual;
        if (visual != null) {
            visual.toFront();
        }

        sequentialTransition.play();
        sequentialTransition.setOnFinished(e -> {
            position = targets[targets.length - 1];
            if (this.visual != null) {
                onComplete.run();
            }
        });
    }

    /**
     * Just a normal getter. ¯\_(ツ)_/¯
     * @return The position of the player. May be {@code null}.
     */
    Point2D getPosition() {
        if (position == null) {
            return null;
        }

        return position;
    }

    /**
     * Just a normal getter. ¯\_(ツ)_/¯
     * @return The color of the player.
     */
    Color getColor() {
        return color.getValue();
    }

    /**
     * Select the player.
     * @param selectionColor The color of the selection border.
     */
    void select(Color selectionColor) {
        final Rectangle visual = this.visual;
        if (visual != null) {
            visual.setStroke(selectionColor);
            visual.toFront();
        }
    }

    /**
     * Deselect the player. This will remove the selection border.
     */
    void deselect() {
        final Rectangle visual = this.visual;
        if (visual != null) {
            visual.setStroke(Color.TRANSPARENT);
        }
    }

    /**
     * Destroy the player visual.
     * This will unbind the player's position from the board and remove the player from the canvas.
     * This method is safe to call at any time.
     */
    void destroy() {
        Platform.runLater(() -> {
            visual.xProperty().unbind();
            visual.yProperty().unbind();

            Pane parent = (Pane) visual.getParent();
            if (parent != null) {
                parent.getChildren().remove(visual);

                parent.requestLayout();
                parent.layout();
            }
        });
    }
}
