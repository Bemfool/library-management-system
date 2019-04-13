package LibraryManagementSystem.controller;

import LibraryManagementSystem.Main;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterUIController implements Initializable {
    private Main application;
    @Override
    public void initialize(URL url, ResourceBundle rb){ }
    public void setApp(Main app) {
        this.application = app;
    }

    public void register(ActionEvent actionEvent) {
    }

    public void goBack(ActionEvent actionEvent) {
    }

    public void enterKey(KeyEvent keyEvent) {
    }
}
