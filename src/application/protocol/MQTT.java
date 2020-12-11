package application.protocol;

import java.io.*;
import java.net.Socket;

public class MQTT {
    private Socket socket;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    private FileInputStream fileInputStream = null;
    private FileOutputStream fileOutputStream = null;

    public MQTT(Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
    }

    public void sendText(String mess) {
        try {
            this.dataOutputStream.writeUTF(mess);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveText() {
        String mess = "";
        try {
            mess = dataInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mess;
    }

    public void sendFile(String filePath) {

    }

    public void receiveFile(String filePath) {

    }

    public void close() throws IOException {
//        sendText("@quit@");
//        this.dataInputStream.close();
//        this.dataOutputStream.close();
        this.socket.close();
//        this.fileInputStream.close();
//        this.fileOutputStream.close();
    }


}