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

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
   // NewClient client;

//    @FXML
//    public void initialize() {
//        handleRefresh(); // get last version of the document
//
//        // Activate "Delete Line" option when a line is selected
//        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, string, newValue) -> {
//            deleteLineMenuItem.setDisable(newValue == null);
//
//            // For editing selected line
//            if (newValue != null) {
//                textField.setText(newValue);
//            }
//        });
//        //client =new NewClient("localhost",1234);
//        //System.out.println("socket ouvert");
//
//    }
    @FXML
    public void initialize(){
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textField.setText(newValue);
                if (deleteLineMenuItem != null) {
                    deleteLineMenuItem.setDisable(false);
                }
            }
        });
        try{
            socket=new Socket("localhost",1234);
            out=new PrintWriter(socket.getOutputStream(),true);
            in=new BufferedReader(new InputStreamReader((socket.getInputStream())));
            new Thread(this::ecouterServeur).start();
            out.println("GETD");
        }catch(IOException e){e.printStackTrace();}
    }
    private void ecouterServeur(){
        try{
            String message;
            while((message=in.readLine())!=null){
                final String msg=message;
                javafx.application.Platform.runLater(()->traiterMessageServeur(msg));
            }
        }catch (IOException e){System.out.println("Connexion perdue");

        }
    }
    private void traiterMessageServeur(String msg) {
        if (msg.startsWith("LINE 0 ")) {
            listView.getItems().clear();
        }

        try {
            if (msg.startsWith("LINE ")) {
                String[] parts = msg.split(" ", 3);
                if (parts.length == 3) {
                    listView.getItems().add(parts[2]);
                }
            }
            else if (msg.startsWith("ADDL ")) {
                String[] p = msg.split(" ", 3);
                int index = Integer.parseInt(p[1]);
                if (index >= 0 && index <= listView.getItems().size()) {
                    listView.getItems().add(index, p[2]);
                }
            }
            else if (msg.startsWith("RMVL ")) {
                int index = Integer.parseInt(msg.split(" ")[1]);
                if (index >= 0 && index < listView.getItems().size()) {
                    listView.getItems().remove(index);
                }
            }
            else if (msg.startsWith("MDFL ")) {
                String[] p = msg.split(" ", 3);
                int index = Integer.parseInt(p[1]);
                if (index >= 0 && index < listView.getItems().size()) {
                    listView.getItems().set(index, p[2]);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du traitement du message : " + msg);
        }
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
        if (out != null) {
            out.println("ADDL " + insertIndex + " (New Line)");
        }
    }

    @FXML
    private void handleDeleteLine() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1 && out!=null){
            out.println("RMVL "+selectedIndex);
        }

    }

    @FXML
    private void handleTextFieldUpdate() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1 && out!=null) {
            String newText=textField.getText();
            out.println("MDFL "+selectedIndex+" "+newText);
        }
        }



    @FXML
    private void  handleRefresh() {
        if (out != null) out.println("GETD");
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

