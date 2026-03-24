package amu.editor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerDispatch {
    private static List<Integer> portsServeurs=new ArrayList<>();
    private static int indexTour=0;
    public static void main(String[] args){
        int portDispatch=1233;
        try(BufferedReader br=new BufferedReader(new FileReader("peers.cfg"))){
            String line;
            while((line=br.readLine())!=null){
                if(line.trim().startsWith("master")||line.trim().startsWith("peers")){
                    String[] parts = line.split("=");
                    String[] addr = parts[1].trim().split(" ");
                    portsServeurs.add(Integer.parseInt(addr[1]));
                }
            }
            System.out.println("Ficher peers.cfg lu par le dispatch "+portsServeurs.size()+" serveurs trouvés.");
        } catch (IOException e) {
            System.out.println("Fichier de configuration peers.cfg introuvable,");
            return;
        }
        if(portsServeurs.isEmpty()){
            System.out.println("Aucun serveur configuré");
            return;
        }
        try(ServerSocket dispatchSocket=new ServerSocket(portDispatch)){
            System.out.println("Serveur dispatch démarré sur le port "+portDispatch);
            System.out.println("En attente de client a rédiriger ... ");
            while(true){
                Socket client=dispatchSocket.accept();
                PrintWriter out=new PrintWriter(client.getOutputStream(),true);
                int portAttribue=portsServeurs.get(indexTour);
                indexTour=(indexTour+1)%portsServeurs.size();
                out.println("REDIRECT localhost "+portAttribue);
                System.out.println("Nouveau client rédirigé vers le port :"+portAttribue);
                client.close();
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
