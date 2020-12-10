package Client;

import java.io.*;
import java.net.*;
 
public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private Client client;
 
    public ReadThread(Socket _socket, Client _client) {
        this.socket = _socket;
        this.client = _client;
 
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
 
    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                System.out.println("\n" + response);
                if (client.getUserName() != null) {
                    System.out.print(client.getUserName() + ": ");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                break;
            }
        }
    }
}