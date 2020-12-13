package Server.ServerFile;

import java.io.EOFException;
import java.io.File;
import java.net.Socket;

import Protocol.MQTT;
import Protocol.Message;

public class ServerFileThread extends Thread {

    private ServerFile server;
    // private final Socket socket;
    private MQTT mqtt;
    private String userName;

    public ServerFileThread(Socket socket, ServerFile server) {
        this.mqtt = new MQTT(socket);
        this.server = server;
    }

    public void sendFile(String filePath) {
        mqtt.sendFile(filePath);
    }

    public void receiveFile(String filePath, long fileSize) {
        mqtt.receiveFile(filePath, fileSize);
    }

    boolean authentication(String roomName) {
        if (this.server.serverChat.hasRoomChat(roomName))
            return true;
        return false;
    }

    public String createFilePath(String roomName, String fileName) {
        int copy = 0;
        String filePath = null;
        do {
            if (copy == 0) {
                filePath = ServerFile.FOLDER_PATH + roomName + "_" + this.userName + "_" + fileName;
            } else {
                filePath = ServerFile.FOLDER_PATH + roomName + "_" + this.userName + "_(" + String.valueOf(copy) + ")"
                        + fileName;
            }

            if (hasFile(filePath)) {
                copy++;
            } else {
                break;
            }
        } while (true);

        return filePath;
    }

    public boolean hasFile(String filePath) {
        File f = new File(filePath);
        if (f.isFile())
            return true;
        return false;
    }

    public long getFileSize(String filePath) {
        File f = new File(filePath);
        return f.length();
    }

    @Override
    public void run() {

        try {
            this.userName = mqtt.receiveMess().getContent();
            String roomName = mqtt.receiveMess().getContent();

            if (!authentication(roomName)) {
                mqtt.sendMess(new Message("t", "404 Not found room chat"));
                return;
            }
            
            Message message = mqtt.receiveMess();
            System.out.println(message.getContent());
    
            if(message.getHeader().equals("c")) {
                if(message.getContent().equals("UPLOAD")) {
    
                    mqtt.sendMess(new Message("t", "200 OK Upload"));
        
                    String fileName = mqtt.receiveMess().getContent();
                    mqtt.sendMess(new Message("t", "210 OK File name"));
                    long fileSize = Long.parseLong(mqtt.receiveMess().getContent());
                    mqtt.sendMess(new Message("t", "210 OK File size"));
        
                    String filePath = createFilePath(roomName, fileName);
                    mqtt.receiveFile(filePath, fileSize);
        
                } else if (message.equals("DOWNLOAD")) {
        
                    mqtt.sendMess(new Message("t", "200 OK Download"));
        
                    String fileName = mqtt.receiveMess().getContent();
                    mqtt.sendMess(new Message("t", "210 OK File name"));
        
                    String filePath = createFilePath(roomName, fileName);
        
                    if(hasFile(filePath)) {
        
                        long fileSize = getFileSize(filePath);
                        this.mqtt.sendMess(new Message("t", String.valueOf(fileSize)));
            
                        mqtt.sendFile(filePath);
                    } else {
                        this.mqtt.sendMess(new Message("t", "404 Not found"));
                    }
                }
            
            }
        } catch(EOFException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
