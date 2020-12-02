package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

class testFile {
    static final String FOLDER_PATH = "Server/database/";
    private Set<RoomChat> roomList = new HashSet<>();

    public static void main(String[] args) throws ClassNotFoundException {
        RoomChat room1 = new RoomChat("Trang's room");
        RoomChat room2 = new RoomChat("Minh's room");
        try {
            FileOutputStream fileOut = new FileOutputStream(new File(FOLDER_PATH + "RoomList.txt"));
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            
            objectOut.writeInt(2);
            objectOut.writeObject(room1);
            objectOut.writeObject(room2);
            objectOut.close();
            System.out.println("The Object  was succesfully written to a file");

            FileInputStream fileIn = new FileInputStream(new File(FOLDER_PATH + "RoomList.txt"));
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            System.out.println(objectIn.readInt());
            RoomChat a = (RoomChat) objectIn.readObject();
            System.out.println(a.getName()); 
            RoomChat b = (RoomChat) objectIn.readObject();
            System.out.println(b.getName()); 
            objectIn.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("File not found: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("IO exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}