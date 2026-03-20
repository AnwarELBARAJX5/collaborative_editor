package amu.editor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static amu.editor.ServerCentral.gererClient;

public class ServerCentralPush {
    private static List<String> documentPartage = Collections.synchronizedList(new ArrayList<>());
    private static List<PrintWriter> clients=Collections.synchronizedList(new ArrayList<>());
    public static void main(String[] args){
        documentPartage.add("ANWARRRRRRRRRR");
        documentPartage.add("Saaaaaaatuuurnin");
        try(ServerSocket server=new ServerSocket(1234)){
            System.out.println("Serveur Push démarré sur 1234");
            while(true){
                Socket s=server.accept();
                new Thread(()->gererClient(s)).start();
            }
        }catch (IOException e){e.printStackTrace();}
    }
    private static void gererClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            clients.add(out);
            String requete;
            while ((requete = in.readLine()) != null) {
                if (requete.equals("GETD")) {
                    for (int i = 0; i < documentPartage.size(); i++) {
                        out.println("LINE " + i + " " + documentPartage.get(i));
                    }
                    out.println("DONE");
                } else {
                    traiterCommande(requete);
                    diffuser(requete);
                }
            }
        } catch (IOException e) {
            System.out.println("Un client est parti.");
        }
    }

    private static void traiterCommande(String cmd) {
        synchronized (documentPartage) {
            try {
                if (cmd.startsWith("ADDL ")) {
                    String[] p = cmd.split(" ", 3);
                    documentPartage.add(Integer.parseInt(p[1]), p[2]);
                } else if (cmd.startsWith("RMVL ")) {
                    documentPartage.remove(Integer.parseInt(cmd.split(" ")[1]));
                } else if (cmd.startsWith("MDFL ")) {
                    String[] p = cmd.split(" ", 3);
                    documentPartage.set(Integer.parseInt(p[1]), p[2]);
                }
            } catch (Exception e) { System.out.println("Commande malformée : " + cmd); }
        }
    }

    private static void diffuser(String message) {
        synchronized (clients) {
            for (PrintWriter out : clients) {
                out.println(message);
            }
        }
    }
}
