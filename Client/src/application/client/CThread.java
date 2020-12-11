package application.client;

import application.protocol.MQTT;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

public class CThread implements Runnable{
    private final Client client;
    private final Socket socket;
    private boolean running = true;
    private MQTT mqtt;

    public CThread(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
        try {
            mqtt = new MQTT(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        sendMessage("Username");
        if (mqtt.receiveText().equals("200 OK"))
            sendMessage(this.client.getName());
        while (running) {
            String mess = null;
            mess = mqtt.receiveText();
//            client.getMain().enableChat();
            if (mess.equals("210 Create successfully room")) {
                showAlert("Room create successfully!");
                client.getMain().enableChat();
            } else if (mess.equals("410 Room already exists")) {
                showAlert("Room already exists!");
            } else if (mess.equals("210 OK room")) {
                showAlert("Join room success!");
                client.getMain().enableChat();
            } else if (mess.equals("404 Not Found")) {
                showAlert("Room not found!");
            } else {
                client.getMain().appendChat("\n" + mess, true);
            }
        }


    }

    public void showAlert(String mess) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                client.getMain().showAlert(mess);
            }
        });
    }

    public void sendMessage(String mess) {
        this.mqtt.sendText(mess);
    }

    public void close() throws IOException {
        this.running = false;
        this.mqtt.close();
    }

}
