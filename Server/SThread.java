package Server;

import java.io.IOException;
import java.net.Socket;

import Protocol.MQTT;

public class SThread extends Thread {

    final int LOGIN_ROOM = 0;
    final int CHAT = 1;

    private String username;
    private String roomName;

    private Server server;
    private final Socket socket;
    private MQTT mqtt;

    private RoomChat roomChat;

    public SThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            this.mqtt = new MQTT(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getroomName() {
        return roomName;
    }

    /**
     * 
     * @param username
     * @param roomName
     * @return true if user in room else return false
     */
    boolean authentication(String roomName) {
        if(this.server.hasRoomChat(roomName)) return true;
        return false;
    }

    void sendText(String message) {
        mqtt.sendText(message);
    }

    /**
     * Print user online in room
     */
    void printUsersOnline() {
        if (roomChat.hasUsersOnline()) {
            mqtt.sendText("Users online in room: " + roomChat.getUsersOnline());
        } else {
            mqtt.sendText("No other users online in room");
        }
    }

    public void chatInRoom() {
        printUsersOnline();

        String serverMessage = "The new user " + username + " enters group";
        roomChat.broadcast(serverMessage, this);

        // start conversation and end chat when client says "bye"
        String clientMessage;
        do {
            clientMessage = mqtt.receiveText();
            serverMessage = username + ": " + clientMessage;
            roomChat.broadcast(serverMessage, this);

        } while (!clientMessage.equals("bye"));

        try {
            roomChat.removeUserThread(this);
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error in SThread: " + ex.getMessage());
            ex.printStackTrace();
        }

        // notify the clients that 1 user has just left
        serverMessage = username + " has quitted.";
        System.out.println(serverMessage);
        roomChat.broadcast(serverMessage, this);
    }

    public RoomChat createRoomChat(String roomName) {
        RoomChat newRoom = new RoomChat(roomName);
        server.roomList.add(newRoom);
        return newRoom;
    }

    @Override
    public void run() {
        //receive client's username to login app
        if(mqtt.receiveText() == "Username"){
            mqtt.sendText("200 OK");
            this.username = mqtt.receiveText();
        }

        //reveive client's request
        int status = LOGIN_ROOM;
        do{
            String message = mqtt.receiveText();
            
            //check user quit app
            if(message == "@quit@"){
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error when close socket in Sthread: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            switch (status) {
                case LOGIN_ROOM:
                    if(message == "CreateRoom"){
                        mqtt.sendText("200 OK");
                        this.roomName = mqtt.receiveText();
                        mqtt.sendText("210 OK room name");
                        if(this.server.hasRoomChat(roomName)){
                            mqtt.sendText("410 Room already exists");
                        } else {
                            this.roomChat = createRoomChat(this.roomName);
                            mqtt.sendText("210 Create successfully room");
                            status = CHAT;
                        }
                    }
                    else if(message == "JoinRoom"){
                            mqtt.sendText("200 OK");
                            String roomName = mqtt.receiveText();
                            if(authentication(roomName)){
                                this.roomChat = server.getRoomChat(roomName);
                                this.roomChat.addUser(this);
                                this.roomName = roomName;
                                mqtt.sendText("210 OK room");
                                status = CHAT;
                            } else {
                                mqtt.sendText("400 Not Found");
                            }  
                        }
                    break;
                case CHAT:
                        chatInRoom();
                        status = LOGIN_ROOM;
                    break;
            }
        } while(true);
    }
}
