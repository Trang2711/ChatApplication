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

    public Server(int port) {
        try {
            this.serverSock = new ServerSocket(port);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error when create server socket: " + e.getMessage());
        }
    }

    public Server() {
        this(DEFAULT_PORT);
    }

    public void start(){
        try {
            while (true) {
                System.out.println("Waiting for client on port " + DEFAULT_PORT + "...");
                Socket connSock = serverSock.accept();
                SThread newUser = new SThread(this, connSock);
                newUser.start();
                System.out.println("Connecting to new client.");            
            }
        } catch (IOException e) {
            System.out.println("Error when accept connect from client: " + e.getMessage());
        }
    }

    public Set<RoomChat> getRoomList() {
        return roomList;
    }

    public RoomChat getRoomChat(String roomName) {
        for (RoomChat roomChat : this.roomList) {
            if(roomChat.getName().equals(roomName)) return roomChat;
        }
        return null;
    }

    public boolean hasRoomChat(String roomName) {
        for (RoomChat roomChat : this.roomList) {
            if(roomChat.getName().equals(roomName)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
