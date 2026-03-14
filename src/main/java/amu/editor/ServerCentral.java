package amu.editor;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerCentral {

    private static AtomicInteger nombreClients = new AtomicInteger(0);
    private static AtomicInteger nbthread =  new AtomicInteger(5);
    List<Path> paths = new ArrayList<>();

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(1234)) {

            ExecutorService pool = Executors.newFixedThreadPool(nbthread.get());

            while (true) {
                Socket client = server.accept();
                int total = nombreClients.incrementAndGet();
                System.out.println("Client connecté. Total = " + total);
                pool.execute(() -> gererClient(client));
            }
        } catch (IOException e) {
            System.out.println("Erreur de connexion");
        }
    }
    private static void gererClient(Socket client) {
        if (nombreClients.get() == nbthread.get()) {
            System.out.println("nbtotal");
        }
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream())
            );

            while (in.readLine() != null) {
                System.out.println("zoba zoba");
            }

        } catch (IOException e) {
            System.out.println("Client déconnecté");
        } finally {
            int total = nombreClients.decrementAndGet();
            System.out.println("Client parti. Total = " + total);
            try {
                client.close();
            } catch (IOException ignored) {}
        }
    }
}