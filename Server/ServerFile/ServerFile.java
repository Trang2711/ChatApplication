package Server.ServerFile;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Server.ServerChat.ServerChat;

public class ServerFile {
    
    private static int DEFAULT_PORT = 8000;
    static final String FOLDER_PATH = "./Server/database/";

    private ServerSocket serverSock;
    public ServerChat serverChat;

    public ServerFile(ServerChat serverChat) {
        try {
            this.serverSock = new ServerSocket(DEFAULT_PORT);
            this.serverChat = serverChat;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error when create server file socket: " + e.getMessage());
        }
    }

    public void start(){
        try {
            while (true) {
                System.out.println("Waiting for client on port " + DEFAULT_PORT + "...");
                Socket connSock = this.serverSock.accept();
                ServerFileThread newUser = new ServerFileThread(connSock, this);
                newUser.start();
                System.out.println("Connecting to new client.");            
            }
        } catch (IOException e) {
            System.out.println("Error when accept connect from client: " + e.getMessage());
        }
    }
}
