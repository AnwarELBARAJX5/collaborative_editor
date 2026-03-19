package amu.editor;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientController {

    @FXML
    private ListView<String> listView;

//    String[] textSample = { "FIRST WITCH  When shall we three meet again?\n",
//            "   In thunder, lightning, or in rain?\n",
//            "SECOND WITCH  When the hurly-burly’s done\n",
//            "   When the battle’s lost and won.\n",
//            "THIRD WITCH  That will be ere the set of sun\n"};
    public Path curentfile;

    @FXML
    private TextField textField;

    @FXML
    private MenuItem deleteLineMenuItem;

   // NewClient client;

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
        //client =new NewClient("localhost",1234);
        //System.out.println("socket ouvert");

    }

    @FXML
    private void handleAddLine() throws IOException {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        int insertIndex;
        String text="(New Line)";
        if (selectedIndex == -1) {
            insertIndex=listView.getItems().size();
        } else {
            insertIndex=selectedIndex+1;
        }
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("ADDL " +insertIndex + " " +text);
        } catch (IOException e) {
            System.out.println("Erreur(AddLine):" + e.getMessage());
        }
        handleRefresh();
    }

    @FXML
    private void handleDeleteLine() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            try (Socket socket = new Socket("localhost", 1234);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println("RMVL " + selectedIndex);
            } catch (IOException e) {
                System.out.println("Erreur (DeleteLine):" + e.getMessage());
            }
            handleRefresh();
        }

    }

    @FXML
    private void handleTextFieldUpdate() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String newText = textField.getText();
            try (Socket socket = new Socket("localhost", 1234);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println("MDFL " + selectedIndex + " " + newText);
            } catch (IOException e) {
                System.out.println("Erreur(UpdateLine):" + e.getMessage());
            }
            handleRefresh();
        }
        }



    @FXML
    private void  handleRefresh() {
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("GETD");
            listView.getItems().clear();
            String reponse;
            while ((reponse = in.readLine()) != null) {
                if (reponse.startsWith("LINE ")) {
                    String[] parts = reponse.split(" ", 3);
                    if (parts.length == 3) {
                        listView.getItems().add(parts[2]);
                    } else if (parts.length == 2) {
                        listView.getItems().add(""); // Cas d'une ligne vide
                    }
                } else if (reponse.startsWith("DONE")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Refresh impossible de contacter le serveur");
        }
    }


    @FXML
    private void handleNewFile() {
        // A faire après
        System.out.println("Bouton New File cliqué");
    }

    @FXML
    private void handlesendFile() {
        //A faire après
        System.out.println("Bouton Send File cliqué");
    }


}

