package amu.editor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ClientHandler implements Runnable {
  private final Socket socket;

  public ClientHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try (Socket s = socket;
         DataInputStream in = new DataInputStream(s.getInputStream());
         DataOutputStream out = new DataOutputStream(s.getOutputStream())) {

      Code code = new Code(new Random());

      out.writeUTF(Code.getCOLORS());

      while (true) {
        String msg = in.readUTF();

        Code guess = new Code(msg.toUpperCase());
        int correct = code.numberOfColorsWithCorrectPosition(guess);
        int incorrect = code.numberOfColorsWithIncorrectPosition(guess);

        if (correct == 4) {
          out.writeUTF("félicitations");
          break;
        } else {
          out.writeUTF("correcte:" + correct + " inncorect:" + incorrect);
        }
      }

    } catch (IOException e) {
      System.out.println("Client déconnecté / erreur: " + e.getMessage());
    }
  }
}
