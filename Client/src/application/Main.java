package application;

import application.client.Client;
import application.view.LoginController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import application.view.Controller;

import java.io.IOException;

public class Main extends Application {
    private Stage primaryStage;
    private Client client;
    private Controller controller;
    private LoginController loginController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/login.fxml"));
        Parent root = loader.load();
        this.loginController = (LoginController) loader.getController();
        this.loginController.setMain(this);
        System.out.println(this.loginController.name);
        this.primaryStage.setTitle("Hello World");
        this.primaryStage.setScene(new Scene(root));
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
    }

    public void changeChatScene() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/sample.fxml"));
        Parent root = loader.load();
        this.controller = (Controller) loader.getController();
        this.controller.init();
        this.controller.setMain(this);
        System.out.println(controller.name);
        this.primaryStage.setScene(new Scene(root));
    }

    public void appendChat(String mess, boolean doesUserSend) {
        this.controller.appendChat(mess, doesUserSend);
    }

    public void initClient(int port, String userName) {
        if (port != 0)
            this.client = new Client(port, userName, this);
        else
            this.client = new Client(userName, this);

        try {
            this.client.start();
            changeChatScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("IOException");
        }
    }

    public void showAlert(String alertMess) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(alertMess);
        alert.setHeaderText("Results:");
        alert.setContentText("Connect to the database successfully!");

        alert.showAndWait();
    }

    public void showDialog(boolean create) {
        TextInputDialog inputDialog;
        if (create) {
            inputDialog = new TextInputDialog("Room ID");
            inputDialog.setTitle("Create");
            inputDialog.setHeaderText("Type Room ID:");
        } else {
            inputDialog = new TextInputDialog("Room ID");
            inputDialog.setTitle("Join");
            inputDialog.setHeaderText("Type Room ID:");
        }
        Button okButton = (Button) inputDialog.getDialogPane().lookupButton(ButtonType.OK);
        TextField inputField = inputDialog.getEditor();
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!inputField.getText().trim().isEmpty()) {
                    if (create) {
                        System.out.println("Create");
                        client.sendMessage("CreateRoom");
                        client.sendMessage(inputField.getText().trim());
                    } else {
                        System.out.println("Join");
                        client.sendMessage("JoinRoom");
                        client.sendMessage(inputField.getText().trim());
                    }
                }
            }
        });

        inputDialog.showAndWait();
    }

    public void enableChat() {
        this.controller.enableChat();
    }

    @Override
    public void stop() throws Exception {
        this.client.close();
        System.out.println("App closed");
        super.stop();
    }

    public Client getClient() {
        return client;
    }

    public static void main(String[] args) {
        launch(args);

    }
}
