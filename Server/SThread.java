package Server;

import java.io.IOException;
import java.net.Socket;

import Protocol.MQTT;

public class SThread extends Thread {

    final int LOGIN_APP = 0;
    final int LOGIN_ROOM = 1;
    final int CHAT = 2;

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
        if (this.server.hasRoomChat(roomName))
            return true;
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

    void leftRoomChat() { 
        if(roomChat != null){
            roomChat.removeUserThread(this);
        }
        
        String serverMessage = username + " has quitted the room chat.";
        roomChat.broadcast(serverMessage, this);
    }

    /**
     * @return 1 if user end a conversation
     * return -1 if user close app
     */
    public int chatInRoom() {
        printUsersOnline();

        String serverMessage = "The new user " + username + " enters group";
        roomChat.broadcast(serverMessage, this);

        // start conversation and end chat when client says "bye"
        String clientMessage;
        do {
            clientMessage = mqtt.receiveText();
            System.out.println(clientMessage);
            serverMessage = username + ": " + clientMessage;
            roomChat.broadcast(serverMessage, this);

            if (clientMessage.equals("@quit@")) {
                try {
                    socket.close();
                    leftRoomChat();
                    System.out.println(username + " has quitted the app");
                    return -1;
                } catch (IOException e) {
                    System.out.println("Error when close socket in Sthread: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } while (!clientMessage.equals("bye"));
       

        // notify the clients that 1 user has just left
        leftRoomChat();
        System.out.println(username + " has quitted the room");
        return 1;     
    }

    public RoomChat createRoomChat(String roomName) {
        RoomChat newRoom = new RoomChat(roomName);
        server.roomList.add(newRoom);
        return newRoom;
    }

    @Override
    public void run() {
        int status = LOGIN_APP;
        do {
            String message = mqtt.receiveText();
            System.out.println(message);

            // check user quit app
            if (message.equals("@quit@")) {
                try {
                    socket.close();
                    if(roomChat != null){
                        roomChat.removeUserThread(this);
                    }
                    System.out.println(username + " has quitted the app");
                } catch (IOException e) {
                    System.out.println("Error when close socket in Sthread: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    break;
                }
            }

            if(status == LOGIN_APP) {
                // receive client's username to login app
                if (message.equals("Username")) {
                    mqtt.sendText("200 OK");
                    this.username = mqtt.receiveText();
                    System.out.println(this.username);
                    status = LOGIN_ROOM;
                    continue;
                }
            }

            if(status == LOGIN_ROOM){
                if (message.equals("CreateRoom")) {
                    // mqtt.sendText("200 OK");
                    this.roomName = mqtt.receiveText();
                    // mqtt.sendText("210 OK room name");
                    if (this.server.hasRoomChat(roomName)) {
                        mqtt.sendText("410 Room already exists");
                        System.out.println("Room already exists");
                    } else {
                        this.roomChat = createRoomChat(this.roomName);
                        this.roomChat.addUser(this);
                        
                        mqtt.sendText("210 Create room successfully");
                        System.out.println("Create room successfully");
                        status = CHAT;
                    }
                } else if (message.equals("JoinRoom")) {
                    // mqtt.sendText("200 OK");
                    String roomName = mqtt.receiveText();
                    if (authentication(roomName)) {
                        this.roomChat = server.getRoomChat(roomName);
                        this.roomChat.addUser(this);
                        this.roomName = roomName;
                        mqtt.sendText("210 OK room");
                        System.out.println("Join room OK");
                        status = CHAT;
                    } else {
                        mqtt.sendText("404 Not Found");
                        System.out.println("Room not found");
                    }
                }
            }

            if(status == CHAT) {
                System.out.println("it got here");
                if(chatInRoom() == -1) break;
                status = LOGIN_ROOM;
            }

        } while (true);
    }
}
