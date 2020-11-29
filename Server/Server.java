package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class Server {
    private static int DEFAULT_PORT = 9000;
    private static final String FOLDER_PATH = "Server/";

    private ServerSocket serverSock;
    private ArrayList<RoomChat> roomList = new ArrayList<>();
    
    public Server(int port) throws IOException {
        this.serverSock = new ServerSocket(port);
    }

    public Server() throws IOException {
        this(DEFAULT_PORT);
    }
    
    public void startServer() throws IOException {
        while (true) {
            System.out.println("Waiting for client...");
            Socket connSock = serverSock.accept();
            SThread sThread = new SThread(connSock, userID, roomChat);
            System.out.println("Connecting to client.");
        }
    }

}
