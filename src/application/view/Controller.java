package application.view;

import application.Main;
import application.protocol.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {
    public String name = "Main Controller";
    private String curSender = null;
    private Main main;
    @FXML
    private TextField textField;
    @FXML
    private MenuItem buttonCreate;
    @FXML
    private MenuItem buttonJoin;
    @FXML
    private MenuItem upload;
    @FXML
    private ScrollPane chatContainer;
//    @FXML
//    private TextArea textChat;

    @FXML
    private VBox textChat;
//    private Client client = new Client();

    @FXML
    public void sendMessage(ActionEvent ae) {
//        System.out.println("You: " + textField.getText());
        appendChat("\nYou: " + textField.getText(), true);
//        appendFile("\nYou: " + textField.getText(), true);
        this.main.getClient().sendMessage(new Message("t", textField.getText()));
        textField.setText("");
//        textChat.setText(textField.getText());
//        client.hello();
    }

    @FXML
    public void sendFile(ActionEvent ae) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(this.main.getPrimaryStage());
        if (file != null) {
//            System.out.println(file.getPath());
            this.main.getClient().sendFile(file);
            appendFile("\nYou: " + file.getName(), true);
            this.main.getClient().sendMessage(new Message("f", file.getName()));
        }
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
//        textChat.appendText(mess);
        String sender = getSender(mess);

        Label label = new Label(getContent(mess));
        HBox hBox = new HBox();

        label.setWrapText(true);
        if (doesUserSend) {
            if (sender.equals(curSender))
                label.getStyleClass().add("chatBubbleCurRight");
            else
                label.getStyleClass().add("chatBubbleNewRight");
            hBox.setAlignment(Pos.BASELINE_RIGHT);
        } else {
            if (sender.equals(curSender))
                label.getStyleClass().add("chatBubbleCurLeft");
            else
                label.getStyleClass().add("chatBubbleNewLeft");
            hBox.setAlignment(Pos.BASELINE_LEFT);
        }
//        label.setAlignment(Pos.CENTER_LEFT);
//        this.textChat.getChildren().add(label);
        hBox.getChildren().add(label);
        if (!sender.equals(curSender)) {
            HBox senderName = new HBox();
            if (doesUserSend) {
                senderName.setAlignment(Pos.BASELINE_RIGHT);
            } else {
                senderName.setAlignment(Pos.BASELINE_LEFT);
            }
            senderName.getChildren().add(new Label(sender));
            this.textChat.getChildren().add(senderName);
        }
        this.textChat.getChildren().add(hBox);
        this.chatContainer.vvalueProperty().bind(this.textChat.heightProperty());
        this.curSender = sender;
    }

    public void appendFile(String fileName, boolean doesUserSend) {
        System.out.println("Head of appendFile");
        String sender = getSender(fileName);

        Label label = new Label(getContent(fileName));
        HBox hBox = new HBox();

        label.setUnderline(true);
        label.setWrapText(true);
        if (doesUserSend) {
            if (sender.equals(curSender))
                label.getStyleClass().add("chatBubbleCurRight");
            else
                label.getStyleClass().add("chatBubbleNewRight");
            hBox.setAlignment(Pos.BASELINE_RIGHT);
        } else {
            if (sender.equals(curSender))
                label.getStyleClass().add("chatBubbleCurLeft");
            else
                label.getStyleClass().add("chatBubbleNewLeft");
            hBox.setAlignment(Pos.BASELINE_LEFT);
        }
//        label.setAlignment(Pos.CENTER_LEFT);
//        this.textChat.getChildren().add(label);
        hBox.getChildren().add(label);
        if (!sender.equals(curSender)) {
            HBox senderName = new HBox();
            if (doesUserSend) {
                senderName.setAlignment(Pos.BASELINE_RIGHT);
            } else {
                senderName.setAlignment(Pos.BASELINE_LEFT);
            }
            senderName.getChildren().add(new Label(sender));
            this.textChat.getChildren().add(senderName);
        }
        System.out.println("Bottom of appendFile");
        this.textChat.getChildren().add(hBox);
        this.chatContainer.vvalueProperty().bind(this.textChat.heightProperty());
        this.curSender = sender;
        System.out.println("Done appendFile");
        label.setOnMouseClicked(mouseEvent -> {
            getFile(label.getText());
        });

    }

    public void getFile(String fileName) {
//        System.out.println(fileName);
        this.main.getClient().downloadFile(fileName);
    }

    public void enableChat() {
//        this.textChat.setText("");
        this.textChat.setVisible(true);
        this.textField.setDisable(false);
    }

    public String getSender(String mess) {
        String[] parts = mess.split(":");
        return parts[0];
    }

    public String getContent(String mess) {
        return mess.replace(getSender(mess) + ": ", "");
    }

    public void init() {
//        this.textChat.setVisible(false);
//        this.textField.setDisable(true);
//        this.textField.getStyleClass().add("textInput2");
//        this.chatContainer.setContent(this.textChat);
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
