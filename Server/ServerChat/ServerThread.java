package Server.ServerChat;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import Protocol.MQTT;
import Protocol.Message;

public class ServerThread extends Thread {

    final int LOGIN_APP = 0;
    final int LOGIN_ROOM = 1;
    final int CHAT = 2;

    private String username;
    private String roomName;

    private ServerChat server;
    private final Socket socket;
    private MQTT mqtt;

    private RoomChat roomChat;

    public ServerThread(ServerChat server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.mqtt = new MQTT(socket);
    }

    public String getUsername() {
        return username;
    }

    public String getRoomName() {
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

    void sendMessage(Message message) {
        mqtt.sendMess(message);
    }

    /**
     * Print user online in room
     */
    void printUsersOnline() {
        if (roomChat.hasUsersOnline()) {
            Message mess = new Message("t", "Users online in room: " + roomChat.getUsersOnline());
            mqtt.sendMess(mess);
        } else {
            Message mess = new Message("t", "No other users online in room");
            mqtt.sendMess(mess);
        }
    }

    void leftRoomChat() {
        if (roomChat != null) {
            roomChat.removeUserThread(this);
        }

        String serverMessage = username + " has quitted the room chat.";
        roomChat.broadcast(new Message("t", serverMessage), this);
    }

    /**
     * @return 1 if user end a conversation return -1 if user close app
     */
    public int chatInRoom() {
        printUsersOnline();

        String serverMessage = "The new user " + username + " enters group";
        roomChat.broadcast(new Message("t", serverMessage), this);

        // start conversation and end chat when client says "bye"
        Message clientMessage = null;
    
        do {
            try {
                clientMessage = mqtt.receiveMess();
            } catch (EOFException e1) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (clientMessage.getHeader() == "t" && clientMessage.getContent().equals("@quit@")) {
                try {
                    socket.close();
                    leftRoomChat();
                    System.out.println(username + " has quitted the app");
                } catch (IOException e) {
                    System.out.println("Error when close socket in ServerThread: " + e.getMessage());
                    e.printStackTrace();
                }
                return -1;
            } else {
                System.out.println(clientMessage.getContent());
                serverMessage = username + ": " + clientMessage;
                roomChat.broadcast(new Message(clientMessage.getHeader(), serverMessage), this);
            }

        } while (!clientMessage.getContent().equals("bye"));

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
        try {
            do {
                Message message = mqtt.receiveMess();
                System.out.println(message.getContent());

                // check user quit app
                if (message.getHeader() == "t" && message.getContent().equals("@quit@")) {
                    try {
                        socket.close();
                        if (roomChat != null) {
                            roomChat.removeUserThread(this);
                        }
                        System.out.println(username + " has quitted the app");
                    } catch (IOException e) {
                        System.out.println("Error when close socket in ServerThread: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                }

                if (status == LOGIN_APP) {
                    // receive client's username to login app
                    if (message.getHeader() == "c" && message.getContent().equals("Username")) {

                        mqtt.sendMess(new Message("t", "200 OK"));

                        Message mess = mqtt.receiveMess();
                        if (mess.getHeader() == "t") {
                            this.username = mess.getContent();
                        }
                        System.out.println(this.username);

                        status = LOGIN_ROOM;
                        continue;
                    }
                }

                if (status == LOGIN_ROOM) {
                    if (message.getHeader() == "c") {
                        if (message.getContent().equals("CreateRoom")) {

                            this.roomName = mqtt.receiveMess().getContent();

                            if (this.server.hasRoomChat(roomName)) {

                                mqtt.sendMess(new Message("t", "410 Room already exists"));
                                System.out.println("Room already exists");

                            } else {

                                this.roomChat = createRoomChat(this.roomName);
                                this.roomChat.addUser(this);

                                mqtt.sendMess(new Message("t", "210 Create room successfully"));
                                System.out.println("Create room successfully");

                                status = CHAT;
                            }
                        } else if (message.getContent().equals("JoinRoom")) {

                            String roomName = mqtt.receiveMess().getContent();

                            if (authentication(roomName)) {

                                this.roomChat = server.getRoomChat(roomName);
                                this.roomChat.addUser(this);
                                this.roomName = roomName;

                                mqtt.sendMess(new Message("t", "210 OK room"));
                                System.out.println("Join room OK");

                                status = CHAT;

                            } else {

                                mqtt.sendMess(new Message("t", "404 Not Found"));
                                System.out.println("Room not found");

                            }
                        }
                    }

                }

                if (status == CHAT) {

                    System.out.println("it got here");
                    if (chatInRoom() == -1)
                        break;

                    status = LOGIN_ROOM;

                }

            } while (true);

        } catch (EOFException e) {
            try {
                socket.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
}
