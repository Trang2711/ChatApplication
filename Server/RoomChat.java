package Server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RoomChat implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private Set<String> usersOnline = new HashSet<>();
    private Set<SThread> userThreads = new HashSet<>();
    String logRoomPath;

    public RoomChat(String roomName) {
        this.name = roomName;
        this.logRoomPath = Server.FOLDER_PATH + roomName + ".txt";
    }

    void addUser(SThread userThread) {
        boolean added = usersOnline.add(userThread.getUsername());
        if(added){
            userThreads.add(userThread);
        }
    }

    void removeUserThread(SThread userThread) {
        boolean removed = usersOnline.remove(userThread.getUsername());
        if (removed) {
            userThreads.remove(userThread);
            System.out.println("The user " + userThread.getUsername() + " quitted");
        }
    }

    Set<String> getUsersOnline() {
        return this.usersOnline;
    }

    boolean hasUsersOnline() {
        return !this.usersOnline.isEmpty();
    }

    public String getName() {
        return name;
    }

    /**
     * 
     * @param userName
     * @return true if userName exsit in file log else return false
     */
    boolean hasUser(String userName){
        
        return true;
    }

    void broadcast(String message, SThread currentUser) {
        for (SThread user : userThreads) {
            if (user != currentUser) {
                user.sendText(message);
            }
        }
    }

}
