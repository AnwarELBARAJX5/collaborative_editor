package amu.editor;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class ClientController {

    @FXML
    private ListView<String> listView;

    String[] textSample = { "FIRST WITCH  When shall we three meet again?\n",
            "   In thunder, lightning, or in rain?\n",
            "SECOND WITCH  When the hurly-burly’s done\n",
            "   When the battle’s lost and won.\n",
            "THIRD WITCH  That will be ere the set of sun\n"};

    @FXML
    private TextField textField;

    @FXML
    private MenuItem deleteLineMenuItem;

    @FXML
    public void initialize() {
        handleRefresh(); // get last version of the document

        // Activate "Delete Line" option when a line is selected
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, string, newValue) -> {
            deleteLineMenuItem.setDisable(newValue == null);

            // For editing selected line
            if (newValue != null) {
                textField.setText(newValue);
            }
        });
    }

    @FXML
    private void handleAddLine() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            listView.getItems().add("(New Line)");
        } else {
            // new line added below selected line
            listView.getItems().add(selectedIndex+1, "(New Line)");
        }
        //TODO request server to add a new line
    }

    @FXML
    private void handleDeleteLine() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            listView.getItems().remove(selectedIndex);
        }
        // TODO request server to remove line
    }

    @FXML
    private void handleTextFieldUpdate() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String newText = textField.getText();
            listView.getItems().set(selectedIndex, newText);
            update(selectedIndex, newText);
        }

    }

    @FXML
    private void  handleRefresh() {
        // TODO request server last version of the document

        listView.getItems().clear();
        for(String line : textSample){
            listView.getItems().add(line);
        }
    }

    private void update(int index, String string) {
        if (index < textSample.length) {
            textSample[index] = string;
        } else if (index == textSample.length) {
            String[] newArray = new String[textSample.length + 1];

            for (int i = 0; i < textSample.length; i++) {
                newArray[i] = textSample[i];
            }

            newArray[index] = string;
            textSample = newArray;
        } else {
            throw new IndexOutOfBoundsException("Index trop grand");
        }
    }

}

