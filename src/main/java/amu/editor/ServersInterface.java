package amu.editor;

import java.net.Socket;

public interface ServersInterface {
    public void gererClient(Socket client);
    public int getPort();

}
