package amu.editor;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.List;

public class Servers {
List<Integer> portsDistants;
int myPort;
public Servers(List<Integer> portsDistants, int myPort){
    this.portsDistants = portsDistants;
    this.myPort = myPort;
}
    public void connect(int portCible) {
        try (
                Socket socket = new Socket("localhost", portCible);
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Connecté au serveur fédéré sur le port "+portCible);
            out.println("OPEN default.txt");
            String requeteDuServeur;
            while ((requeteDuServeur=in.readLine())!=null){
                System.out.println("Reçu du serveur " + portCible + " : " + requeteDuServeur);if (requeteDuServeur.startsWith("ADDL") || requeteDuServeur.startsWith("RMVL") || requeteDuServeur.startsWith("MDFL")) {
                    ServerCentralPush.traiterCommandeFederation("default.txt", requeteDuServeur);
                }
            }
        } catch (IOException e) {
            System.out.println("Connexion arrêtée avec le serveur " +portCible);
        }
    }

    public void connextionAll(){
          for(int portCible: portsDistants){
              new Thread(()->{
                  if(portCible !=myPort)
                        connect(portCible);
              }) .start();
          }


    }



}
