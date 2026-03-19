package amu.editor;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerCentral {

    private static AtomicInteger nombreClients = new AtomicInteger(0);
    private static AtomicInteger nbthread =  new AtomicInteger(5);
    public static List<String> documentPartage = Collections.synchronizedList(new ArrayList<>());
    public static void main(String[] args) {
        documentPartage.add("Saturninnnnnnnnnnn");
        documentPartage.add("Anwarrrrrrrrrrrrrrr");
        documentPartage.add("Saaaaaaattttttgitj");
        try (ServerSocket server = new ServerSocket(1234)) {
            ExecutorService pool = Executors.newFixedThreadPool(nbthread.get());
            while (true) {
                Socket client = server.accept();
                int total = nombreClients.incrementAndGet();
                System.out.println("Client connecté.Total=" + total);
                pool.execute(() -> gererClient(client));
            }
        } catch (IOException e) {
            System.out.println("Erreur de connexion");
        }
    }
    public static void gererClient(Socket client) {
        if (nombreClients.get() == nbthread.get()) {
            System.out.println("nbtotal");
        }
        try (  BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
               PrintWriter out=new PrintWriter(client.getOutputStream(),true);)
        {
            String requete;
            while ((requete = in.readLine()) != null) {
                System.out.println("Reçue du client:" + requete);
                if (requete.equals("GETD")) {
                    synchronized (documentPartage) {
                        for (int i = 0; i < documentPartage.size(); i++) {
                            out.println("LINE " + i + " " + documentPartage.get(i));
                        }
                        out.println("DONE " + documentPartage.size());
                    }
                } else if (requete.startsWith("ADDL")) {
                    String[] parts = requete.split(" ", 3);
                    if (parts.length >= 3) {
                        int index = Integer.parseInt(parts[1]);
                        String texte = parts[2];
                        if (index >= 0 && index <= documentPartage.size()) {
                            documentPartage.add(index, texte);
                        }
                    }

                } else if (requete.startsWith("RMVL")) {
                    String[] parts = requete.split(" ", 2);
                    if (parts.length == 2) {
                        int index = Integer.parseInt(parts[1]);
                        if (index >= 0 && index < documentPartage.size()) {
                            documentPartage.remove(index);
                        }
                    }

                } else if (requete.startsWith("MDFL ")) {
                    // On découpe en 3 parties max : "MDFL", "index", "texte"
                    String[] parts = requete.split(" ", 3);
                    if (parts.length >= 3) {
                        int index = Integer.parseInt(parts[1]);
                        String texte = parts[2];
                        if (index >= 0 && index < documentPartage.size()) {
                            documentPartage.set(index, texte);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client déconnecté ou erreur de format");
        } finally {
            int total = nombreClients.decrementAndGet();
            System.out.println("Client parti.Total=" + total);
            try {
                client.close();
            } catch (IOException ignored) {}
        }
    }
}