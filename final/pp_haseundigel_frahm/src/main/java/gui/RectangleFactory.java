package gui;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 * This is a helper class that provides methods to create rectangles.
 * It is used to create the game board fields and player visuals. <br>
 * <br>
 * The generated rectangles are bound to the {@link Pane} they are added to.
 * This means that they will automatically resize when the pane is resized.
 * @author Lukas Frahm
 */
class RectangleFactory {

    /**
     * The width of a canvas / (board image)
     */
    private final double width;

    /**
     * The height of a canvas / (board image)
     */
    private final double height;

    /**
     * The width of a canvas / (board image) as a property.
     */
    private final DoubleProperty fitWidth;

    /**
     * The height of a canvas / (board image) as a property.
     */
    private final DoubleProperty fitHeight;

    /**
     * The canvas the rectangles are added to.
     * Not necessarily the same as the canvas the rectangles are bound to.
     */
    private final Pane canvas;

    /**
     * Create a new rectangle factory.
     * @param width The width of a canvas / (board image)
     * @param height The height of a canvas / (board image)
     * @param fitWidth The width of a canvas / (board image) as a property.
     * @param fitHeight The height of a canvas / (board image) as a property.
     * @param canvas The canvas the rectangles are added to.
     *               Not necessarily the same as the canvas the rectangles are bound to.
     */
    RectangleFactory(double width, double height, DoubleProperty fitWidth, DoubleProperty fitHeight, Pane canvas) {
        this.width = width;
        this.height = height;
        this.fitWidth = fitWidth;
        this.fitHeight = fitHeight;
        this.canvas = canvas;
    }

    /**
     * Creates multiple rectangles with the given parameters and binds them to {@link #fitWidth} and {@link #fitHeight}.
     * The rectangles are transparent and have no stroke.
     * @param midpoints The midpoints of the rectangles.
     * @param size The size of the rectangles.
     * @param strokeWidth The stroke width of the rectangles.
     * @param arcSize The arc size of the rectangles.
     * @return The created rectangles. The order of the rectangles is the same as the order of the midpoints.
     */
    @SuppressWarnings("SameParameterValue")
    Rectangle[] createRectangles(Point2D[] midpoints, double size, double strokeWidth, double arcSize) {
        Rectangle[] rectangles = new Rectangle[midpoints.length];

        for (int i = 0; i < rectangles.length; i++) {
            Rectangle rect = createRectangle(midpoints[i], size, strokeWidth, arcSize);
            rectangles[i] = rect;
            canvas.getChildren().add(rect);
        }

        return rectangles;
    }

    /**
     * Creates a rectangle with the given parameters and binds it to {@link #fitWidth} and {@link #fitHeight}.
     * The rectangle is transparent and has no stroke.
     * @param midpoint The midpoint of the rectangle.
     * @param size The size of the rectangle.
     * @param strokeWidth The stroke width of the rectangle.
     * @param arcSize The arc size of the rectangle.
     * @return The created rectangle.
     */
    Rectangle createRectangle(Point2D midpoint, double size, double strokeWidth, double arcSize) {
        Rectangle rect = new Rectangle(0, 0);

        rect.setFill(Color.TRANSPARENT);

        double relativeWidth = size / width;
        double relativeHeight = size / height;
        double relativeArcSize = arcSize / width;

        // Bind the rectangle so it resizes correctly
        rect.xProperty().bind(fitWidth.multiply((midpoint.getX() / width) - relativeWidth / 2));
        rect.yProperty().bind(fitHeight.multiply((midpoint.getY() / height) - relativeHeight / 2));
        rect.widthProperty().bind(fitWidth.multiply(relativeWidth));
        rect.heightProperty().bind(fitHeight.multiply(relativeHeight));
        rect.strokeWidthProperty().bind(fitWidth.multiply(strokeWidth / width));
        rect.arcWidthProperty().bind(fitWidth.multiply(relativeArcSize));
        rect.arcHeightProperty().bind(fitHeight.multiply(relativeArcSize));

        return rect;
    }
}
