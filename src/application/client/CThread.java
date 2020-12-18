package application.client;

import application.protocol.MQTT;
import application.protocol.Message;
import javafx.application.Platform;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class CThread implements Runnable{
    private final Client client;
    private final Socket socket;
    private boolean running = true;
    private MQTT mqtt;

    public CThread(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
        this.mqtt = new MQTT(socket);
    }

    /**
     * send file here!
     */
//    public void sendFile(String pathRes, String pathDes) {
    public void uploadFile(File fileRes, String pathDes) {
        try {
            Socket fSocket = new Socket(InetAddress.getLocalHost(), 8000);
            System.out.println("Connected to the server on 8000");

            MQTT mqtt = new MQTT(fSocket);
            System.out.println("Connected to the server");

            mqtt.sendMess(new Message("c", "UPLOAD"));
            if (mqtt.receiveMess().getContent().equals("200 OK Upload")) {

                //send file path to save on server
                mqtt.sendMess(new Message("t", pathDes));

                //send file size to server file
                String fileSize = String.valueOf(fileRes.length());
                mqtt.sendMess(new Message("t", fileSize));

                //sent content file
                mqtt.sendFile(fileRes);

                String mess = mqtt.receiveMess().getContent();
                if (mess.equals("210 Upload Completed")) {
                    /**
                     * alert download success
                     */
                    fSocket.close();
                } else if (mess.equals("410 Upload failed")) {
                    /**
                     * alert download success
                     */
                    fSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * receive file here!
     */
//    public void downloadFile(String pathRes, String pathDes) {
//        try {
//            Socket fSocket = new Socket(InetAddress.getLocalHost(), 8000);
//            System.out.println("Connected to the server on 8000");
//
//            MQTT mqtt = new MQTT(fSocket);
//            System.out.println("Connected to the server");
//
//            mqtt.sendMess(new Message("c", "DOWNLOAD"));
//            if(mqtt.receiveMess().getContent().equals("200 OK Download")) {
//
//                //send file path to save on server
//                mqtt.sendMess(new Message("t", pathRes));
//
//                //receive file size to server file
//                long fileSize = Long.parseLong(mqtt.receiveMess().getContent());
//
//                //receive content file
//                mqtt.receiveFile(pathDes, fileSize);
//
//                String mess = mqtt.receiveMess().getContent();
//                if (mess.equals("210 Download Completed")) {
//                    /**
//                     * alert download success
//                     */
//                    fSocket.close();
//                } else if (mess.equals("410 Download failed")) {
//                    /**
//                     * alert download success
//                     */
//                    fSocket.close();
//                }
//
//            } else if(mqtt.receiveMess().getContent().equals("404 Not found")) {
//                /**
//                 * to do something
//                 */
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public long getFileSize(String filePath) {
        File f = new File(filePath);
        return f.length();
    }

    @Override
    public void run() {
        sendMessage(new Message("c", "UserName"));
        try {
            if (mqtt.receiveMess().getContent().equals("200 OK")){
                sendMessage(new Message("t", this.client.getName()));
                if(! mqtt.receiveMess().getContent().equals("210 OK")) return;
            }

            while (running) {
                Message mess = null;
                mess = mqtt.receiveMess();
                System.out.println(mess.getContent());

                if(mess.getHeader().equals("t")) {
                    if (mess.getContent().equals("210 OK created")) {
                        showAlert("Room created successfully!");
                        client.getMain().enableChat();
                    } else if (mess.getContent().equals("400 Bad request")) {
                        showAlert("Room already exists!");
                    } else if (mess.getContent().equals("210 OK joined")) {
                        showAlert("Join room success!");
                        client.getMain().enableChat();
                    } else if (mess.getContent().equals("410 Not Found")) {
                        showAlert("Room not found!");
                    } else {
                        appendChat(mess.getContent());
                    }
                } else if(mess.getHeader().equals("f")) {
                    //mess content is "trang:filename";
//                    String[] tmp = mess.getContent().split(":");
//                    String owner = tmp[0];
//                    String fileName = tmp[1];
//                    System.out.println("Owner: " + owner);
//                    System.out.println("File name: " + fileName);
                    this.client.getMain().appendFile(mess.getContent(), false);
                }

            }

        } catch (EOFException e) {
            System.out.println("Error: Message is null: " + e.getMessage() );
            e.printStackTrace();
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

    public void appendChat(String mess) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                client.getMain().appendChat(mess, false);
            }
        });
    }

    public void sendMessage(Message mess) {
        if (mess != null) {
            this.mqtt.sendMess(mess);
        }
    }

    public void close() throws IOException {
        this.running = false;
        this.socket.close();
    }

}
