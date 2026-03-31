package amu.editor;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientBotTest {
    public static void main(String[] args){
        int nombreDeMessages=1000;
        int nbBots=1000;
        for (int b = 1; b <=nbBots; b++) {
            final int botId = b;
            // On lance chaque bot dans un Thread séparé pour qu'ils attaquent en même temps
            new Thread(() -> lancerUnBot(botId, nombreDeMessages)).start();
        }
    }

    public static void lancerUnBot(int id,int nombreDeMessages){
        try{
            Socket dispatchSocket=new Socket("localhost",1233);
            BufferedReader dispatchIn=new BufferedReader(new InputStreamReader(dispatchSocket.getInputStream()));
            String reponseDispatch=dispatchIn.readLine();
            dispatchSocket.close();
            int portCible=1234;
            if (reponseDispatch != null && reponseDispatch.startsWith("REDIRECT ")) {
                portCible = Integer.parseInt(reponseDispatch.split(" ")[2]);
            }
            System.out.println("Bot connecté au port "+portCible);
            Socket socket=new Socket("localhost",portCible);
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("OPEN stress_test.txt");
            System.out.println("Début de l'envoi de "+nombreDeMessages+" messages");
            long startTime=System.currentTimeMillis();
            for(int i=0;i<nombreDeMessages;i++){
                out.println("ADDL "+i+" ligne généré par le bot "+i);
            }
            long endTime=System.currentTimeMillis();
            long durationMs=endTime-startTime;
            double debit=(nombreDeMessages/(double) durationMs)*1000;
            double latence=(double)durationMs/nombreDeMessages;
            System.out.println("Test terminé");
            System.out.println("temps total "+durationMs+"ms");
            System.out.println("débit ="+String.format("%.2f",debit)+"operations/secondes");
            System.out.println("Latence par message : "+String.format("%.4f",latence)+" ms");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    }

