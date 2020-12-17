package sample.socket_operation_handeler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connector_3_for_car_list_update {

    private static Connector_3_for_car_list_update connector = null;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    private Connector_3_for_car_list_update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("localhost", 58555);
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static Connector_3_for_car_list_update getInstance()
    {
        if(connector == null)
            connector = new Connector_3_for_car_list_update();

        return connector;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
