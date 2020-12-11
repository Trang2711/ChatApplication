package application.view;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {
    public String name = "Main Controller";
    private Main main;
    @FXML
    private TextField textField;
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonJoin;
    @FXML
    private TextArea textChat;

//    private Client client = new Client();

    @FXML
    public void sendMessage(ActionEvent ae) {
        System.out.println("You: " + textField.getText());
        appendChat("\nYou: " + textField.getText(), false);
        this.main.getClient().sendMessage(textField.getText());
        textField.setText("");
//        textChat.setText(textField.getText());
//        client.hello();
    }

    @FXML
    public void joinRoom() {
        this.main.showDialog(false);
    }

    @FXML
    public void createRoom() {
        this.main.showDialog(true);
    }

    public void appendChat(String mess, boolean doesUserSend) {
        textChat.appendText(mess);
    }

    public void enableChat() {
        this.textChat.setText("");
        this.textChat.setVisible(true);
        this.textField.setDisable(false);
    }

    public void init() {
        this.textChat.setVisible(false);
        this.textField.setDisable(true);
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
