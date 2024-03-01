package gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import logic.Logging.LogMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class provides data that is used by the GUI.
 * This includes the field midpoints, the start positions and the end positions.
 * It also provides methods to load images from the resources.
 *
 * @author Lukas Frahm
 */
class DataProvider {
    /**
     * The field midpoints are the points that are in the middle of each field that is clickable.
     */
    static final Point2D[] fieldMidPoints;

    /**
     * The start positions are the points where the players start.
     */
    static final Point2D[] startPositions;

    /**
     * The end positions are the points where the players end.
     */
    static final Point2D[] endPositions;

    static {
        FieldPositions positions = loadFieldPointsFromJSON("/gui/fieldPoints.json");
        fieldMidPoints = positions.fieldMidPoints;
        startPositions = positions.startPositions;
        endPositions = positions.endPositions;
    }

    /**
     * Loads the field midpoints from the JSON file at the given path.
     * It is assumed that the JSON file contains an array named "fieldMidpoints" of objects
     * with the properties "x" and "y". The values of these properties are the x and
     * y coordinates of the field midpoints.<br>
     * <br>
     * Loads the start positions from the JSON file at the given path.
     * It is assumed that the JSON file contains an array named "startPositions".
     * The values of these properties are the x and y coordinates of the start positions.<br>
     * <br>
     * Loads the end positions from the JSON file at the given path.
     * It is assumed that the JSON file contains an array named "endPositions".
     * The values of these properties are the x and y coordinates of the end positions.<br>
     * <br>
     * @param path The path to the JSON file
     * @return The field midpoints, start positions and end positions
     */
    @SuppressWarnings("SameParameterValue")
    static FieldPositions loadFieldPointsFromJSON(String path) {
        FieldPositions fieldPositions;

        try (InputStream is = DataProvider.class.getResourceAsStream(path)) {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Point2D.class, new Point2DTypeAdapter())
                        .create();
                fieldPositions = gson.fromJson(reader, FieldPositions.class);
            }
        } catch (IOException e) {
            LogMessage.error("Could not load field midpoints from JSON: " + path, e);

            // There is nothing else we could use for the field midpoints, so we have to crash the program
            // We could inform the user about the error, but that would be a bit pointless, since the user
            // can't do anything about it. If this error occurs, the shipped JAR is broken and needs to be
            // replaced.
            throw new RuntimeException(e);
        }

        return fieldPositions;
    }

    /**
     * Loads an image from the resources at the given path.
     * @param path The path to the image
     * @return The image or null if the image could not be loaded
     */
    static Image loadImageFromResources(String path) {
        try (InputStream is = DataProvider.class.getResourceAsStream(path)) {
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            LogMessage.error("Could not load image from resources: " + path, e);
        }

        return null;
    }

    /**
     * This class is used to deserialize the field midpoints, start positions and end positions from JSON.
     */
    private static class FieldPositions {
        public Point2D[] fieldMidPoints;
        public Point2D[] startPositions;
        public Point2D[] endPositions;
    }

    /**
     * A type adapter for the Point2D class.
     */
    private static class Point2DTypeAdapter extends TypeAdapter<Point2D> {

        @Override
        public void write(JsonWriter out, Point2D point2D) throws IOException {
            out.beginObject();
            out.name("x").value(point2D.getX());
            out.name("y").value(point2D.getY());
            out.endObject();
        }

        @Override
        public Point2D read(JsonReader in) throws IOException {
            double x = 0.0;
            double y = 0.0;

            in.beginObject();
            while (in.hasNext()) {
                String name = in.nextName();
                if (name.equals("x")) {
                    x = in.nextDouble();
                } else if (name.equals("y")) {
                    y = in.nextDouble();
                } else {
                    in.skipValue();
                }
            }
            in.endObject();

            return new Point2D(x, y);
        }
    }
}
