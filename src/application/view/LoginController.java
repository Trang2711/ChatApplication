package application.view;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {
    private Main main;
    public String name = "login";

    @FXML
    private TextField hostInput;
    @FXML
    private TextField nameInput;

    @FXML
    public void login(ActionEvent ae) {
        if (nameInput.getText().trim().isEmpty())
            this.main.showAlert("User name haven't been set", "Error");
        else if (hostInput.getText().trim().isEmpty()) {
            this.main.initClient("", nameInput.getText().trim());
            System.out.println(nameInput.getText().trim());
        } else {

            this.main.initClient(hostInput.getText().trim(), nameInput.getText().trim());
            System.out.println(nameInput.getText().trim());
        }

    }


    public void setMain(Main main) {
        this.main = main;
    }
}
