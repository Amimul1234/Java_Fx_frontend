package sample.socket;

import java.io.*;
import java.net.Socket;

public class Connector {

    private static Connector connector = null;
    private Socket socket;

    private Connector() {
        try {
            this.socket = new Socket("localhost", 50000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connector getInstance()
    {
        if(connector == null)
            connector = new Connector();

        return connector;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
