package application.view;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    private Main main;
    public String name = "login";

    @FXML
    private TextField portInput;
    @FXML
    private TextField nameInput;

    @FXML
    public void login(ActionEvent ae) {
        if (nameInput.getText().trim().isEmpty())
            this.main.showAlert("User name haven't been set");
        else if (portInput.getText().trim().isEmpty()) {
            this.main.initClient(0, nameInput.getText().trim());
            System.out.println(nameInput.getText().trim());
        } else {

            this.main.initClient(Integer.parseInt(portInput.getText().trim()), nameInput.getText().trim());
            System.out.println(nameInput.getText().trim());
        }

    }


    public void setMain(Main main) {
        this.main = main;
    }
}
