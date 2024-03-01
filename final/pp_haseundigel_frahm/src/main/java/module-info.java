module pp.haseundigel.frahm {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires junit;

    opens gui to javafx.fxml, com.google.gson; // to find UIController in gui and fxml-files in resources/gui
    opens logic to junit, com.google.gson;     // to allow testing the logic and usage of gson

    exports gui;
    opens logic.Logging to com.google.gson, junit;
}