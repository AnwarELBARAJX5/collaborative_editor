package amu.editor;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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
        int insertIndex;
        String text="(New Line)";
        if (selectedIndex == -1) {
            insertIndex=listView.getItems().size();
            listView.getItems().add(text);
        } else {
            insertIndex=selectedIndex+1;
            listView.getItems().add(insertIndex, text);
        }
        try(java.net.Socket socket=new java.net.Socket("localhost",1234) ;
            java.io.DataOutputStream out=new java.io.DataOutputStream(socket.getOutputStream())) {
                out.writeUTF("ADDL "+insertIndex+ " "+text);

        } catch (java.io.IOException e) {
            System.out.println("Erreur de communication avec le serveur :"+e.getMessage());
        }
    }

    @FXML
    private void handleDeleteLine() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            try(Socket socket=new Socket("localhost",1234);
                DataOutputStream out=new DataOutputStream(socket.getOutputStream())){
                out.writeUTF("DELL "+selectedIndex);
            } catch (Exception e) {
                System.out.println("Erreur lors de la suppression :"+e.getMessage());
            }
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

