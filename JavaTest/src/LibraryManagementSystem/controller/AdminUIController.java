package LibraryManagementSystem.controller;

import LibraryManagementSystem.Main;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminUIController implements Initializable {
    private Main application;
    @FXML private ChoiceBox searchOption;

    public void setApp(Main app) {
        this.application = app;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        searchOption.setItems(FXCollections.observableArrayList(
                "书名", "作者", "出版社", "出版日期"
        ));
        searchOption.setValue("书名");
    }
}
