package amu.editor;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class GUIClient extends javafx.application.Application{
    @Override
    public void start(Stage primaryStage) {
        URL url = getClass().getResource("clientView.fxml");
        System.out.println("URL: " + url);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("clientView.fxml"));

        System.out.println(loader.getCharset());
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();

        }

        Scene scene = new Scene(root,300,200);
        primaryStage.setTitle("collaborative editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}



