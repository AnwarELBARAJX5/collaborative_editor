package amu.editor;
import javax.security.auth.login.CredentialException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public  class Gestionfichier {

    public static Path currentFile;
    public static Path  Creation(){
        try {
            Path dossier = Paths.get("documents");
            if (!Files.exists(dossier)) {
                Files.createDirectories(dossier);
            }

            String nomFichier = "document_" + System.currentTimeMillis() + ".txt";
            currentFile = dossier.resolve(nomFichier);

            Files.createFile(currentFile);
            System.out.println("Nouveau fichier créé : " + currentFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentFile;
    }

    public static void write(Path path,String[] strings){
        if (strings.length==0) {
            System.out.println("votre document est vide");
            return;
        }
        else {
            try {
                Files.write(path, Arrays.asList(strings));
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }

    public static String creation(String chemin, Map<UUID,Path> pathMap) throws IOException {
        Path file = Paths.get(chemin);
        Path parent = file.getParent();
        String docId = null;
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        if (!Files.exists(file)) {
            Files.createFile(file);
            System.out.println("Nouveau fichier créé : " + file);
            docId = UUID.randomUUID().toString();
        } else {
            return ("Fichier déjà existant : " + file);
        }
        return docId;
    }

    public static String[] addline(int x, String s, String[] ss) {
        String[] newArray = new String[ss.length + 1];
        for (int i = 0, j = 0; i < newArray.length; i++) {
            if (i == x) {
                newArray[i] = s;
            } else {
                newArray[i] = ss[j];
                j++;
            }
        }

        return newArray;
    }
    public static String[] deleteline(int x,String[] ss){
        String[] newArray = new String[ss.length-1];
        for (int i = 0, j = 0; i < newArray.length; i++) {
            if (i == x) {
                //lol
            } else {
                newArray[i] = ss[j];
                j++;
            }
        }
        return newArray;
    }
    public static void sauvegarderFichierServeur(String fileName,List<String> lignes){
        try{java.nio.file.Path file=java.nio.file.Paths.get("Serveur_autosave_text");
        if(!java.nio.file.Files.exists(file)){
            java.nio.file.Files.createDirectories(file);
        }
        java.nio.file.Path fichier=file.resolve(file);
        java.nio.file.Files.write(fichier,lignes);

    }catch(IOException e){
            System.out.println("Erreur lors de la sauvegarde coté serveur:"+e.getMessage());
    }
    }
}
