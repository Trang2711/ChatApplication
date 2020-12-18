package application.client;

import application.Main;
import application.protocol.Message;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private static final int DEFAULT_PORT = 9000;
    private static final int FILE_PORT = 8000;

    private String hostName = null;
    private String name;
    private InetAddress host;
    private Socket connSock;
    private CThread cThread;

    private Main main;

    public Client(String hostName, String name, Main main) {
        this.hostName = hostName;
        this.name = name;
        this.main = main;
    }

    public Client(String name, Main main) {
        this.name = name;
        this.main = main;
    }

    public void start() throws IOException {
        this.host = InetAddress.getLocalHost();
        if (hostName != null)
            this.connSock = new Socket(hostName, DEFAULT_PORT);
        else
            this.connSock = new Socket(host.getHostAddress(), DEFAULT_PORT);

        this.cThread = new CThread(this, connSock);
        Thread thread = new Thread(cThread);
        thread.start();
    }

    public void close() {
        try {
            this.cThread.close();
//            this.connSock.close();
            System.out.println("Close connection!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(String fileName, String fileID) {
        try {
            FThread fThread = new FThread(this, fileName, fileID);
            fThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message mess) {
        this.cThread.sendMessage(mess);
    }

    public void sendFile(File file, String fileID) {
        this.cThread.uploadFile(file, fileID);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Main getMain() {
        return main;
    }
}
