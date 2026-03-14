package amu.editor;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

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
}
