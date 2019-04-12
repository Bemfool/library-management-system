package LibraryManagementSystem;

import LibraryManagementSystem.controller.AdminUIController;
import LibraryManagementSystem.controller.LoginUIController;
import LibraryManagementSystem.controller.UserUIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {
    public static int id;
    public static Connection conn;
    public Stage stage;
    public Stage floatStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Library Management System");
        gotoLoginUI();
        stage.show();
    }

    public void gotoLoginUI() throws Exception {
        stage.setResizable(false);
        LoginUIController loginUI = (LoginUIController)replaceSceneContent("fxml/LoginUI.fxml");
        loginUI.setApp(this);
    }

    public void gotoUserUI() throws Exception {
        stage.setResizable(true);
        UserUIController userUI = (UserUIController)replaceSceneContent("fxml/UserUI.fxml");
        userUI.setApp(this);
    }

    public void gotoAdminUI() throws Exception {
        stage.setResizable(true);
        AdminUIController adminUI = (AdminUIController)replaceSceneContent("fxml/AdminUI.fxml");
        adminUI.setApp(this);
    }


    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = LibraryManagementSystem.Main.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(LibraryManagementSystem.Main.class.getResource(fxml));
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

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Library?serverTimezone=GMT&&useSSL=false",
                "root", "lyz5621617");

        launch(args);
    }
}
