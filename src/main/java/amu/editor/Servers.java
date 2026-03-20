package amu.editor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Servers {
List<ServersInterface> serversInterfaceList;
int myPort;
public Servers(List<ServersInterface> serversInterfaceList, int myPort){
    this.serversInterfaceList = serversInterfaceList;
    this.myPort = myPort;
}
    public void connect(ServersInterface b) {
        try (
                Socket socket = new Socket("localhost", b.getPort());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream())
        ) {
            while (true) {
                out.writeUTF("Request from server");
                String response = in.readUTF();
                System.out.println("Response from " + b.getPort() + ": " + response);

                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Connexion arrêtée avec le serveur " + b.getPort());
            e.printStackTrace();
        }
    }

    public void connextionAll(){
          for(ServersInterface ser: serversInterfaceList){
              ServersInterface target = ser;
              new Thread(()->{
                  if(target.getPort() !=myPort)
                        connect(target);
              }) .start();
          }


    }



}
