module amu.editor {
    requires javafx.controls;
    requires javafx.fxml;


    opens amu.editor to javafx.fxml;
    exports amu.editor;
}