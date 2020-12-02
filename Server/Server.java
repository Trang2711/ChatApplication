package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private static int DEFAULT_PORT = 9000;
    static final String FOLDER_PATH = "Server/database/";

    private ServerSocket serverSock;
    public Set<RoomChat> roomList = new HashSet<>();

    public Server(int port) throws IOException {
        this.serverSock = new ServerSocket(port);
    }

    public Server() throws IOException {
        this(DEFAULT_PORT);
    }

    public void startServer() throws IOException {
        while (true) {
            System.out.println("Waiting for client on port " + DEFAULT_PORT + "...");
            Socket connSock = serverSock.accept();

            SThread newUser = new SThread(this, connSock);
            newUser.start();
            System.out.println("Connecting to new client.");
        }
    }

    public Set<RoomChat> getRoomList() {
        return roomList;
    }

}
