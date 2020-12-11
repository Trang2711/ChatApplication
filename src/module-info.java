module Demo.JavaFx2 {
    requires  javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    opens  application;
    opens  application.view;
}