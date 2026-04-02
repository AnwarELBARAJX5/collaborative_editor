package amu.editor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils{
    public static List<Integer> getAllPorts() {
        List<Integer> ports = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("peers.cfg"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("master") || line.trim().startsWith("peer")) {
                    String[] addresse = line.split("=")[1].trim().split(" ");
                    ports.add(Integer.parseInt(addresse[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Fichier de configuration introuvable");
        }
        return ports;
    }

    public static int getMasterPort(){
        try(BufferedReader br=new BufferedReader(new FileReader("peers.cfg"))){
            String line;
            while((line=br.readLine())!=null){
                return Integer.parseInt(line.split("=")[1].trim().split(" ")[1]);
            }
        }catch(IOException e){}
        return 1234;
    }
    public static String getMasterIp(){
        try(BufferedReader br=new BufferedReader(new FileReader("peers.cfg"))){
            String line;
            while((line=br.readLine())!=null){
                if(line.trim().startsWith("master")){
                    return line.split("=")[1].trim().split(" ")[0];
                }
            }
        }catch(IOException e){}
        return "localhost";
    }
}
