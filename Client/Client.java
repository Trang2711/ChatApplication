package Client;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private InetAddress hostname;
    private int port;
    private String userName;

    public Client() throws UnknownHostException {
        this.hostname = InetAddress.getLocalHost();
        this.port = 9090;
    }
 
    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("Connected to the server");
 
            new ReadThread(socket, this).start();
            new WriteThread(socket, this).start();
 
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
 
    }
 
    void setUserName(String userName) {
        this.userName = userName;
    }
 
    String getUserName() {
        return this.userName;
    }
 
 
    public static void main(String[] args) throws UnknownHostException {
        Client client = new Client();
        client.execute();
    }
}