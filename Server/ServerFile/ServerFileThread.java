package Server.ServerFile;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import Protocol.MQTT;
import Protocol.Message;

public class ServerFileThread extends Thread {

    private MQTT mqtt;
    private Socket socket;

    public ServerFileThread(Socket socket) {
        this.mqtt = new MQTT(socket);
        this.socket = socket;
    }

    // public void sendFile(String filePath) {
    //     mqtt.sendFile(filePath);
    // }

    // public void receiveFile(String filePath, long fileSize) {
    //     mqtt.receiveFile(filePath, fileSize);
    // }

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
            
            Message message = mqtt.receiveMess();
            System.out.println(message.getContent());
    
            if(message.getHeader().equals("c")) {
                if(message.getContent().equals("UPLOAD")) {
    
                    mqtt.sendMess(new Message("t", "200 OK Upload"));
        
                    String filePath = mqtt.receiveMess().getContent();
                    long fileSize = Long.parseLong(mqtt.receiveMess().getContent());

                    if(mqtt.receiveFile(filePath, fileSize) == 1) {
                        mqtt.sendMess(new Message("t", "210 Upload Completed"));
                    } else {
                        mqtt.sendMess(new Message("t", "410 Upload failed"));
                    }
                    
        
                } else if (message.equals("DOWNLOAD")) {
        
                    mqtt.sendMess(new Message("t", "200 OK Download"));
        
                    String filePath = mqtt.receiveMess().getContent();
        
                    if(hasFile(filePath)) {
        
                        long fileSize = getFileSize(filePath);
                        this.mqtt.sendMess(new Message("t", String.valueOf(fileSize)));
            
                        if (mqtt.sendFile(filePath) == 1) {
                            mqtt.sendMess(new Message("t", "210 Download Completed"));
                        } else {
                            mqtt.sendMess(new Message("t", "410 Download failed"));
                        }

                    } else {
                        this.mqtt.sendMess(new Message("t", "404 Not found"));
                    }
                }
            
            }
        } catch(EOFException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
				this.socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }

}
