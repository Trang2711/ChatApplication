package Server;

import java.io.IOException;
import java.net.Socket;

import Protocol.MQTT;

public class SThread extends Thread {

    final int LOGIN_APP = 0;
    final int JOIN_ROOM = 1;

    private String username;
    private String roomname;

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

    public String getRoomname() {
        return roomname;
    }

    /**
     * 
     * @param username
     * @param roomName
     * @return true if user in room else return false
     */
    boolean authentication(String username, String roomName) {
        for (RoomChat room : server.getRoomList()) {
            if (room.getName() == roomName) {
                return room.hasUser(username);
            }
        }
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
        // add new user and notify for clients
        roomChat.addUser(this);
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
        //receive client's username
        if(mqtt.receiveText() == "Username"){
            mqtt.sendText("200 OK");
            this.username = mqtt.receiveText();
        }

        //reveive client's request
        int status = LOGIN_APP;
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
                case LOGIN_APP:
                    if(message == "CreateRoom"){
                        mqtt.sendText("200 OK");
                        this.roomname = mqtt.receiveText();
                        mqtt.sendText("210 OK username");
                        createRoomChat(this.roomname);
                        status = JOIN_ROOM;
                    } else if(message == "JoinRoom"){
                        mqtt.sendText("200 OK");
                        this.roomname = mqtt.receiveText();
                        mqtt.sendText("210 OK username");
                        status = JOIN_ROOM;
                    }
                    break;
                case JOIN_ROOM:
                    if(authentication(username, roomname)){
                        mqtt.sendText("200 OK");
                        chatInRoom();
                        status = LOGIN_APP;
                    } else{
                        mqtt.sendText("400 Not Found");
                    }
                    break;
            }
        } while(true);
    }
}
