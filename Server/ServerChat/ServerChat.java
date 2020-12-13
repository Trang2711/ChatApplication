package Server.ServerChat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import Server.ServerFile.ServerFile;

public class ServerChat {
    private static int DEFAULT_PORT = 9000;
    static final String FOLDER_PATH = "Server/database/";

    public ServerFile serverFile;

    private ServerSocket serverSock;
    
    public Set<RoomChat> roomList = new HashSet<>();

    public ServerChat(int defaultPort) {
        try {
            this.serverSock = new ServerSocket(defaultPort);
            this.serverFile = new ServerFile(this);
            this.serverFile.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error when create server socket: " + e.getMessage());
        }
    }

    public ServerChat() {
        this(DEFAULT_PORT);
    }

    public void start(){
        try {
            while (true) {
                System.out.println("Waiting for client on port " + DEFAULT_PORT + "...");
                Socket connSock = this.serverSock.accept();
                ServerThread newUser = new ServerThread(this, connSock);
                newUser.start();
                System.out.println("Waiting for new client...");            
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
        ServerChat server = new ServerChat();
        server.start();
    }
}
