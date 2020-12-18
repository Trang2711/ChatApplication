package application.client;

import application.protocol.MQTT;
import application.protocol.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class FThread extends Thread {
    private final Client client;
    private final Socket socket;
    private boolean running = true;
    private MQTT mqtt;
    private String fileName, fileID;

    public FThread(Client client, String fileName, String fileID) throws IOException {
        this.client = client;
        this.socket = new Socket(InetAddress.getLocalHost(), 8000);
        ;
        this.mqtt = new MQTT(this.socket);
        this.fileName = fileName;
        this.fileID = fileID;
    }

    @Override
    public void run() {
//        super.run();
        downloadFile();
    }

    public void downloadFile() {
        try {
            System.out.println("Connected to the server on 8000");

            System.out.println("Connected to the server");

            mqtt.sendMess(new Message("c", "DOWNLOAD"));
            if (mqtt.receiveMess().getContent().equals("200 OK Download")) {

                //send file path that saved on server
                mqtt.sendMess(new Message("t", this.fileName + "@.@" + this.fileID));

                //receive file name to server file
                String fileName = mqtt.receiveMess().getContent();

                //receive file size to server file
                long fileSize = Long.parseLong(mqtt.receiveMess().getContent());

                //receive content file
                mqtt.receiveFile(fileName, fileSize);

                String mess = mqtt.receiveMess().getContent();
                if (mess.equals("210 Download Completed")) {
                    /**
                     * alert download success
                     */
                    this.socket.close();
                } else if (mess.equals("410 Download failed")) {
                    /**
                     * alert download success
                     */
                    this.socket.close();
                }

            } else if (mqtt.receiveMess().getContent().equals("404 Not found")) {
                /**
                 * to do something
                 */
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
