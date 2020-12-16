package Server.ServerFile;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFile extends Thread{
    
    private static int DEFAULT_PORT = 8000;
    private ServerSocket serverSock;

    public ServerFile() {
        try {
            this.serverSock = new ServerSocket(DEFAULT_PORT);
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
                ServerFileThread newUser = new ServerFileThread(connSock);
                newUser.start();
                System.out.println("Waiting for new client on port 8000...");            
            }
        } catch (IOException e) {
            System.out.println("Error when accept connect from client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServerFile serverFile = new ServerFile();
        serverFile.start();
    }
}
