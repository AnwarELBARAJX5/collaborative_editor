package amu.editor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ServerCentralPush implements ServersInterface{
//    private static List<String> documentPartage = Collections.synchronizedList(new ArrayList<>());
    private static Map<String, List<String>> documents = new ConcurrentHashMap<>();
//    private static List<PrintWriter> clients=Collections.synchronizedList(new ArrayList<>());
    private static Map<PrintWriter, String> clients = new ConcurrentHashMap<>();
    private int port;
    private static boolean isMaster=false;
    private static PrintWriter outToMaster=null;
    private static List<PrintWriter> peersConnectes=Collections.synchronizedList(new ArrayList<>());
    public ServerCentralPush(int port){
        this.port=port;
        String masterIp="localhost";
        int masterPort=1234;
        try(BufferedReader br=new BufferedReader(new FileReader("peers.cfg"))){
            String line;
            while((line=br.readLine())!=null){
                if(line.trim().startsWith("master")){
                    String[] parts = line.split("=");
                    String[] addr = parts[1].trim().split(" ");
                    masterIp = addr[0];
                    masterPort = Integer.parseInt(addr[1]);
                }
            }
            System.out.println("Ficher peers.cfg lu");
        } catch (IOException e) {
            System.out.println("Fichier de configuration peers.cfg introuvable,donc localhost 1234 est le maitre par défaut");
        }
        if(this.port==masterPort){
            isMaster=true;
            System.out.println("Je suis le serveur master sur le port "+this.port);
        }else{
            System.out.println("Je suis pas maitre sur le port "+this.port);
            connecterAuMaster(masterIp,masterPort);
        }

    }
    @Override
    public int getPort() {
        return port;
    }

    public static void main(String[] args){
//        List<String> defaut = Collections.synchronizedList(new ArrayList<>());
//        defaut.add("Bienvenue dans l'éditeur collaboratif");
//        defaut.add("Fichier : default.txt");
//        documents.put("default.txt", defaut);
//        try(ServerSocket server=new ServerSocket(1234)){
//            System.out.println("Serveur Multi fichiers démarré sur 1234");
//            while(true){
//                Socket s=server.accept();
//                new Thread(()->gererClient(s)).start();
//            }
//        }catch (IOException e){e.printStackTrace();}
//
    int portLocal=1234;
    if(args.length >= 1)portLocal=Integer.parseInt(args[0]);
    List<String> defaut=Collections.synchronizedList(new ArrayList<>());
    defaut.add("Bienvenue sur le serveur fédéré (Port "+portLocal+")");
    documents.put("default.txt",defaut);
    ServerCentralPush monServeur=new ServerCentralPush(portLocal);

        // Lancement de notre propre serveur
        try (ServerSocket server = new ServerSocket(portLocal)) {
            System.out.println("🚀 Serveur démarré sur le port " + portLocal);
            while (true) {
                Socket s = server.accept();
                new Thread(() -> monServeur.gererClient(s)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public  void gererClient(Socket socket) {
        PrintWriter out=null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter outFinal = new PrintWriter(socket.getOutputStream(), true)) {
            out=outFinal;
            String requete;
            while ((requete = in.readLine()) != null) {
                System.out.println("Reçue : " + requete);
                if (requete.equals("IAMPEER")) {
                    if (isMaster) peersConnectes.add(outFinal);
                    continue;
                }
                if (requete.startsWith("FORWARD ")){
                    if (isMaster){
                        String[] parts=requete.split(" ", 3);
                        String fileName=parts[1];
                        String cmd=parts[2];

                        traiterCommande(fileName, cmd);
                        diffuser(fileName,cmd); // À nos clients directs
                        diffuserAuxPeers(fileName, cmd); // Aux autres esclaves
                    }
                    continue;
                }
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
                        if(isMaster) {
                            traiterCommande(fileName, requete);
                            diffuser(fileName, requete);
                            diffuserAuxPeers(fileName,requete);
                        }else{
                            if(outToMaster!=null){
                                outToMaster.println("FORWARD "+fileName+" "+requete);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Un client ou un serveur s'est déconnecté.");
        } finally {
            if(out != null) {clients.remove(out);peersConnectes.remove(out);}
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
    public static void traiterCommandeFederation(String fileName,String cmd){
        traiterCommande(fileName,cmd);
        diffuser(fileName,cmd);
    }
    private static void diffuserAuxPeers(String fileName,String cmd){
        String syncMsg="SYNC "+fileName+" "+cmd;
        for (PrintWriter peerOut : peersConnectes) {
            peerOut.println(syncMsg);
        }
    }
    public static void connecterAuMaster(String ip,int port){
        new Thread(()->{
            try {
                Socket socket = new Socket(ip, port);
                outToMaster = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outToMaster.println("IAMPEER");
                System.out.println("Connecté au serveur master");
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("SYNC ")) {
                        String[] parts = message.split(" ", 3);
                        if (parts.length == 3) {
                            String fileName = parts[1];
                            String commande = parts[2];
                            traiterCommande(fileName, commande);
                            diffuser(fileName, commande);
                        }
                    }
                }

            }catch (IOException e){System.out.println("perte de connexion avec le master");}
        }).start();
    }
//    public static void connecterAutreServeur(String ip,int portAutreServeur) {
//        new Thread(() -> {
//            try {
//                Socket socketVersAutre = new Socket(ip, portAutreServeur);
//                System.out.println("Connecté au serveur fédéré sur le port" + portAutreServeur);
//                BufferedReader in = new BufferedReader(new InputStreamReader(socketVersAutre.getInputStream()));
//                PrintWriter out = new PrintWriter(socketVersAutre.getOutputStream(), true);
//                clients.put(out, "default.txt");
//                String requeteDuServeur;
//                while ((requeteDuServeur = in.readLine()) != null) {
//                    System.out.println("Reçu de l'autre serveur")
//                }
//            }
//
//
//        })
//    }


}
