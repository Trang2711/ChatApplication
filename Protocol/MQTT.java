package Protocol;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MQTT {
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    private FileInputStream fileInputStream = null;
    private FileOutputStream fileOutputStream = null;

    public MQTT(Socket socket) throws IOException {
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendText(String text) {

    }

    public String receiveText() {
        return "";
    }

    public void sendFile(String filePath) {

    }

    public void receiveFile(String filePath) {
        
    }


}