module amu.editor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens amu.editor to javafx.fxml;
    exports amu.editor;
}