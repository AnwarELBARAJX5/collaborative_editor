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
    private static final String SERVER_IP = "localhost";
    private static final int DISPATCH_PORT = 1233;
    private String name="Anonyme";
    @FXML
    private ListView<String> listView;


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
//        //client =new NewClient(SERVER_IP,1234);
//        //System.out.println("socket ouvert");
//
//    }
    @FXML
    public void initialize(){
        javafx.application.Platform.runLater(()->{
            javafx.scene.control.TextInputDialog dialog=new javafx.scene.control.TextInputDialog("Anonyme");
            dialog.setTitle("Identification");
            dialog.setHeaderText("Bienvenue dans l'éditeur KULEBAR");
            dialog.setContentText("Entrez votre nom:");
            java.util.Optional<String> result=dialog.showAndWait();
            result.ifPresent(name-> {
                this.name=name;
            });

        });
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textField.setText(newValue);
                if (deleteLineMenuItem != null) {
                    deleteLineMenuItem.setDisable(false);
                }
            }
        });

        try{
            System.out.println("Demande d'un serveur au Dispatcher...");
            Socket dispatchSocket = new Socket(SERVER_IP, DISPATCH_PORT);
            BufferedReader dispatchIn = new BufferedReader(new InputStreamReader(dispatchSocket.getInputStream()));

            String reponseDispatch = dispatchIn.readLine();
            System.out.println("Reçu du Dispatcher: "+reponseDispatch);
            dispatchSocket.close();
            int portCible = ConfigUtils.getMasterPort();
            if (reponseDispatch!=null&&reponseDispatch.startsWith("REDIRECT ")){
                String[] parts=reponseDispatch.split(" ");
                portCible=Integer.parseInt(parts[2]);
            }
            System.out.println("🚀 Connexion finale au serveur sur le port : " + portCible);
            socket = new Socket(SERVER_IP, portCible);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader((socket.getInputStream())));

            new Thread(this::ecouterServeur).start();
            out.println("OPEN default.txt");

        } catch(IOException e){
            System.out.println("Impossible de se connecter au Dispatcher sur le port 1233.");
            e.printStackTrace();
        }
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
            listView.scrollTo(listView.getItems().size() - 1);
        } catch (Exception e) {
            System.out.println("Erreur lors du traitement du message: " + msg);
        }
    }

    @FXML
    private void handleAddLine()  {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        int insertIndex;
        String text="(New Line)";
        if (selectedIndex == -1) {
            insertIndex=listView.getItems().size();
        } else {
            insertIndex=selectedIndex+1;
        }
        if (out != null) {
            out.println("ADDL " + insertIndex + "["+name+"] Nouvelle ligne");
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
        javafx.scene.control.TextInputDialog dialog=new javafx.scene.control.TextInputDialog("nouveau.txt");
        dialog.setTitle("Gestion des fichiers");
        dialog.setHeaderText("Ouvrir ou créer un document");
        dialog.setContentText("Nom du fichier :");
        java.util.Optional<String> result=dialog.showAndWait();
        if (result.isPresent()) {
            String fileName=result.get();
            if (fileName!=null && !fileName.trim().isEmpty()){
                listView.getItems().clear();
                if (out != null){
                    out.println("OPEN "+fileName);
                    System.out.println("Client change pour le fichier:"+fileName);
                }
            }
        }
    }

    @FXML
    private void handlesendFile() {
        java.util.List<String> lignes=listView.getItems();
        String[] contenu=lignes.toArray(new  String[0]);
        java.nio.file.Path nouveauFichier=Gestionfichier.Creation();
        if(nouveauFichier!=null){
            Gestionfichier.write(nouveauFichier,contenu);
            System.out.println("Fichier sauvegardé avec succès :"+nouveauFichier.toString());
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Sauvegarde réussie");
                alert.setHeaderText("Fichier enregistré");
                alert.setContentText("Le document a été sauvegardé avec succès sur votre ordinateur sous:\n" + nouveauFichier.toString());
                alert.showAndWait();
            });
        }

    }


}

