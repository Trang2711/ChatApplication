package application.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MQTT {

    private static final int BUFFER_SIZE = 1024;

    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;
    // private FileInputStream fileInputStream = null;
    // private FileOutputStream fileOutputStream = null;

    public MQTT(Socket socket) {
        try {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMess(Message mess) {
        try {
            this.dataOutputStream.writeUTF(mess.getMessage());
            System.out.println(mess.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Message receiveMess() throws EOFException{
        String mess = "";
        try {
            mess = dataInputStream.readUTF();
            System.out.println(mess);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Message(mess);
    }

    //    public int sendFile(String filePath) {
    public int sendFile(File file) {

//        File file = new File(filePath);
        if (!file.isFile()) {
            System.out.println("File is not exist");
            return -1;
        }
        long fileSize = (int) file.length();

        long dataRead = 0;
        long dataLeft = 0;

        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            InputStream inputStream = new FileInputStream(file);
            while (dataRead < fileSize) {
                if (fileSize - dataRead > (long) BUFFER_SIZE) {
                    dataRead += inputStream.read(buffer);
                    this.dataOutputStream.write(buffer);
                    // System.out.println(buffer);
                } else {
                    dataLeft = fileSize - dataRead;
                    dataRead += inputStream.read(buffer, 0, (int) dataLeft);
                    this.dataOutputStream.write(buffer, 0, (int) (dataLeft));
                    // System.out.println(buffer);
                }
            }
            System.out.println("Done!");
            inputStream.close();
            return 1;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public int receiveFile(String filePath, long fileSize) {
        long dataRead = 0;
        long dataLeft = 0;

        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            OutputStream outputStream = new FileOutputStream(filePath);
            while (dataRead < fileSize) {
                if (fileSize - dataRead > (long) BUFFER_SIZE) {
                    dataRead += this.dataInputStream.read(buffer);
                    outputStream.write(buffer);
                } else {
                    dataLeft = fileSize - dataRead;
                    dataRead += this.dataInputStream.read(buffer, 0, (int) dataLeft);
                    outputStream.write(buffer, 0, (int) (dataLeft));
                }
            }
            System.out.println("Download complete!");
            outputStream.close();
            return 1;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return -1;
    }

}