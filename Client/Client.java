package Client;

import java.io.Console;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import Protocol.MQTT;
import Protocol.Message;

public class Client {
    private InetAddress hostname;
    private int port;
    private String userName;

    public Client() throws UnknownHostException {
        this.hostname = InetAddress.getLocalHost();
        this.port = 9000;
    }
 
    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);
            System.out.println("Connected to the server");
 
            // Console console = System.console();
            // String userName = console.readLine("\nEnter your name: ");
            Socket fSocket = new Socket(hostname, 8000);
            System.out.println("Connected to the server on 8000");
            MQTT mqtt = new MQTT(fSocket);
            System.out.println("Connected to the server");

            mqtt.sendMess(new Message("ttrang"));
            mqtt.sendMess(new Message("troom1"));
            mqtt.sendMess(new Message("cUPLOAD"));
            
            mqtt.sendMess(new Message("tabc.txt"));
            File f = new File("abc.txt");
            long len = f.length();
            mqtt.sendMess(new Message("t", String.valueOf(len)));
            mqtt.sendFile("abc.txt");



            // new ReadThread(socket, this).start();
            // new WriteThread(socket, this).start();
 
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