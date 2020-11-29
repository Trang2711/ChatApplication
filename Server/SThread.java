package Server;

import java.io.IOException;
import java.net.Socket;

import Protocol.MQTT;

public class SThread implements Runnable {
    private static final int BUFFER_SIZE = 1024;

    private final Socket socket;
    private final RoomChat roomChat;
    private final String userID;
    private MQTT mqtt;

    public SThread(Socket socket, String userID, RoomChat roomChat) {
        this.socket = socket;
        this.userID = userID;
        this.roomChat = roomChat;
        try {
            this.mqtt = new MQTT(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendText(String text) {

    }

    public String receiveText() {
        return "";
    }

    @Override
    public void run() {
        

    }

}
