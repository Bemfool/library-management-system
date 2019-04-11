package StockTradingSystem;

import StockTradingSystem.controller.AdminUIControlller;
import StockTradingSystem.controller.ChangePasswordUIController;
import StockTradingSystem.controller.ClientUIController;
import StockTradingSystem.controller.LoginUIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    public Stage stage;
    public Stage floatStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("B Group Stock Trading System v0.1");
        ClientUIController clientUI = (ClientUIController)replaceSceneContent("fxml/ClientUI.fxml");
        clientUI.setApp(this);
        //gotoLoginUI();
        stage.show();
    }

    public void createChangePasswordUI() throws Exception {
        floatStage = new Stage();
        floatStage.setTitle("Change Password");
        floatStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Main.class.getResourceAsStream("fxml/ChangePasswordUI.fxml");
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource("fxml/ChangePasswordUI.fxml"));
        AnchorPane page;
        try {
            page = loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page);
        floatStage.setScene(scene);
        floatStage.show();
        ChangePasswordUIController changePasswordUI = loader.getController();
        changePasswordUI.setApp(this);
    }

    public void gotoLoginUI() throws Exception {
        stage.setResizable(false);
        LoginUIController loginUI = (LoginUIController)replaceSceneContent("fxml/LoginUI.fxml");
        loginUI.setApp(this);
    }

    public void gotoAdminUI() throws Exception {
        stage.setResizable(true);
        AdminUIControlller AdminUI = (AdminUIControlller)replaceSceneContent("fxml/AdminUI.fxml");
        AdminUI.setApp(this);
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Main.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource(fxml));
        AnchorPane page;
        try {
            page = loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
