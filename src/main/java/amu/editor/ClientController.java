package amu.editor;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientController {

    @FXML
    private ListView<String> listView;

    String[] textSample = { "FIRST WITCH  When shall we three meet again?\n",
            "   In thunder, lightning, or in rain?\n",
            "SECOND WITCH  When the hurly-burly’s done\n",
            "   When the battle’s lost and won.\n",
            "THIRD WITCH  That will be ere the set of sun\n"};
    public Path curentfile;

    @FXML
    private TextField textField;

    @FXML
    private MenuItem deleteLineMenuItem;

    NewClient client;

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
        client =new NewClient("localhost",1234);
        System.out.println("socket ouvert");

    }

    @FXML
    private void handleAddLine() throws IOException {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        int insertIndex;
        String text="(New Line)";
        if (selectedIndex == -1) {
            insertIndex=listView.getItems().size();
            listView.getItems().add(text);
        } else {
            insertIndex=selectedIndex+1;
            listView.getItems().add(insertIndex, text);
            textSample=Gestionfichier.addline(insertIndex,listView.getItems().get(insertIndex),textSample);
        }
    }

    @FXML
    private void handleDeleteLine() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            textSample=Gestionfichier.deleteline(selectedIndex,textSample);
            handleRefresh();
        }

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
         try {
             textSample[index] = string;
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
    }
    @FXML
    private void handleNewFile() {
        try {
            curentfile=Gestionfichier.Creation();
            listView.getItems().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void handlesendFile() {
        if (curentfile == null) {
            System.out.println("Pas de fichier ouvert");
            return;
        }
        try {
           Gestionfichier.write(curentfile,textSample);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

