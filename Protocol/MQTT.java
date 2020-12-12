package Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MQTT {

    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    // private FileInputStream fileInputStream = null;
    // private FileOutputStream fileOutputStream = null;

    public MQTT(Socket socket) throws IOException {
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }   

    public void sendText(String mess) {
        try {
            this.dataOutputStream.writeUTF(mess);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String receiveText(){
        String mess = "";
        try {
            mess = dataInputStream.readUTF();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR when receive Text: " + e.getMessage());
            e.printStackTrace();
            
        }
        return mess;
    }

    public void sendFile(String filePath) {

    }

    public void receiveFile(String filePath) {
        
    }

}