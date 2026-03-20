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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServerCentralPush {
//    private static List<String> documentPartage = Collections.synchronizedList(new ArrayList<>());
    private static Map<String, List<String>> documents = new ConcurrentHashMap<>();
//    private static List<PrintWriter> clients=Collections.synchronizedList(new ArrayList<>());
    private static Map<PrintWriter, String> clients = new ConcurrentHashMap<>();
    public static void main(String[] args){
        List<String> defaut = Collections.synchronizedList(new ArrayList<>());
        defaut.add("Bienvenue dans l'éditeur collaboratif");
        defaut.add("Fichier : default.txt");
        documents.put("default.txt", defaut);
        try(ServerSocket server=new ServerSocket(1234)){
            System.out.println("Serveur Multi fichiers démarré sur 1234");
            while(true){
                Socket s=server.accept();
                new Thread(()->gererClient(s)).start();
            }
        }catch (IOException e){e.printStackTrace();}
    }
    private static void gererClient(Socket socket) {
        PrintWriter out=null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter outFinal = new PrintWriter(socket.getOutputStream(), true)) {
            out=outFinal;
            String requete;
            while ((requete = in.readLine()) != null) {
                System.out.println("Reçue : " + requete);

                if (requete.startsWith("OPEN ")) {
                    String fileName = requete.split(" ", 2)[1];
                    // On enregistre que ce client travaille sur ce fichier
                    clients.put(outFinal, fileName);
                    documents.putIfAbsent(fileName, Collections.synchronizedList(new ArrayList<>()));
                    envoyerDocument(outFinal, fileName);
                }
                else if (requete.startsWith("ADDL ") || requete.startsWith("RMVL ") || requete.startsWith("MDFL ")) {
                    String fileName = clients.get(outFinal);
                    if (fileName != null) {
                        traiterCommande(fileName, requete);
                        diffuser(fileName, requete);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Un client s'est déconnecté.");
        } finally {
            if (out != null) clients.remove(out);
        }
    }

    private static void traiterCommande(String fileName,String cmd) {
        List<String> doc=documents.get(fileName);
        synchronized (doc) {
            try {
                if (cmd.startsWith("ADDL ")) {
                    String[] p = cmd.split(" ", 3);
                    doc.add(Integer.parseInt(p[1]), p[2]);
                } else if (cmd.startsWith("RMVL ")) {
                    doc.remove(Integer.parseInt(cmd.split(" ")[1]));
                } else if (cmd.startsWith("MDFL ")) {
                    String[] p = cmd.split(" ", 3);
                    doc.set(Integer.parseInt(p[1]), p[2]);
                }
            } catch (Exception e) { System.out.println("Commande malformée : " + cmd); }
        }
    }

    private static void envoyerDocument(PrintWriter out,String fileName){
        List<String> lignes=documents.get(fileName);
        synchronized (lignes){
            for(int i=0;i<lignes.size();i++){
                out.println("LINE "+i+" "+lignes.get(i));
            }
            out.println("DONE");
        }
    }
    private static void diffuser(String fileName, String message) {
        for (Map.Entry<PrintWriter, String> entry :clients.entrySet()) {
            PrintWriter clientOut = entry.getKey();
            String fileDuClient = entry.getValue();
            if (fileDuClient != null && fileDuClient.equals(fileName)) {
                clientOut.println(message);
            }
        }
    }

}
