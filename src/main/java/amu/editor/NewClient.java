package amu.editor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewClient {
    public String ip;
    public int port;
    public NewClient(String ip, int port){
        this.ip = ip;
        this.port = port;
        System.out.println("clent1");
    }
    public Socket newSocket() {
        try {
            return new Socket(this.ip, port);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendPacket(Socket socket, Map<Path, List<String>> documents) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeInt(documents.size()); // le client envoit la taille de donnée à envoyer
        for (Map.Entry<Path, List<String>> entry : documents.entrySet()) {
            out.writeUTF(entry.getKey().toString()); //envoit du path
            List<String> list = entry.getValue();
            out.writeInt(list.size());
            for (String s : list) {
                out.writeUTF(s);// envoi des lignes
            }
        }
        out.flush();
    }

    public Map<Path,List<String>> readPacket(Socket socket) throws IOException {
        Map<Path,List<String>> documents = new HashMap<>();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        int nbSize = in.readInt();
        for (int i=0; i<nbSize; i++){
            String pathStr = in.readUTF();
            Path path = Paths.get(pathStr);
            int listSize = in.readInt();
            List<String> list = new ArrayList<>();

            for (int j = 0; j < listSize; j++) {
                list.add(in.readUTF());
            }
            documents.put(path, list);
        }
        return documents;
    }

    public void refresh(Socket socket, Path path)throws IOException{ // cette fonction demande une mise à jour du dochument
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeBoolean(true);
        out.writeUTF(path.toString());

    }

}
