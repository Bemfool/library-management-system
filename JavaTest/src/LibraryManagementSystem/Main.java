package LibraryManagementSystem;

import LibraryManagementSystem.controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

public class Main extends Application {
    public static int id;               /* 用户/管理员账号 */
    public static Connection conn;      /* 用于数据库连接  */
    public Stage stage;
    public Stage floatStage = new Stage();

    @Override
    public void start(Stage primaryStage){
        stage = primaryStage;
        stage.setTitle("Library Management System");
        gotoLoginUI();
        stage.show();
    }


    /* 函数: gotoLoginUI
     * 用法：gotoLoginUI();
     * ------------------------------------------
     * 跳转到登陆界面。
     */

    public void gotoLoginUI(){
        stage.setResizable(false);
        LoginUIController loginUI;
        try {
            loginUI = (LoginUIController)replaceSceneContent("fxml/LoginUI.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            ControllerUtils.showAlert("[错误] 登陆界面无法导入!");
            System.out.println("ERROR::LOGIN_UI::LOAD::FAILED");
            return;
        }
        assert loginUI != null;
        loginUI.setApp(this);
    }


    /* 函数: gotoUserUI
     * 用法：gotoUserUI();
     * ------------------------------------------
     * 跳转到用户主界面。
     */

    public void gotoUserUI(){
        stage.setResizable(true);
        UserUIController userUI;
        try {
            userUI = (UserUIController)replaceSceneContent("fxml/UserUI.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            ControllerUtils.showAlert("[错误] 用户界面无法导入!");
            System.out.println("ERROR::USER_UI::LOAD::FAILED");
            return;
        }
        assert userUI != null;
        userUI.setApp(this);
    }


    /* 函数: gotoAdminUI
     * 用法：gotoAdminUI();
     * ------------------------------------------
     * 跳转到管理员主界面。
     */

    public void gotoAdminUI(){
        stage.setResizable(true);
        AdminUIController adminUI;
        try {
            adminUI = (AdminUIController)replaceSceneContent("fxml/AdminUI.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            ControllerUtils.showAlert("[错误] 管理员界面无法导入!");
            System.out.println("ERROR::ADMIN_UI::LOAD::FAILED");
            return;
        }
        assert adminUI != null;
        adminUI.setApp(this);
    }

    public void displayRegisterUI() {
        floatStage.setResizable(true);
        RegisterUIController registerUI = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            AnchorPane page = null;
            try (InputStream in = Main.class.getResourceAsStream("fxml/RegisterUI.fxml")) {
                loader.setBuilderFactory(new JavaFXBuilderFactory());
                loader.setLocation(Main.class.getResource("fxml/RegisterUI.fxml"));
                page = loader.load(in);
                registerUI = loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert page != null;
            Scene scene = new Scene(page);
            floatStage.setScene(scene);
            floatStage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
            ControllerUtils.showAlert("[错误] 注册界面无法导入!");
            System.out.println("ERROR::ADMIN_UI::LOAD::FAILED");
            return;
        }
        assert registerUI != null;
        registerUI.setApp(this);
        floatStage.show();
    }


    /* 函数: replaceSceneContent
     * 用法：UI = (UIController)replaceSceneContent("fxml/UI.fxml");
     * ------------------------------------------
     * 场景替换。
     */

    private Initializable replaceSceneContent(String fxml){
        FXMLLoader loader = new FXMLLoader();
        AnchorPane page;
        try (InputStream in = Main.class.getResourceAsStream(fxml)) {
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Main.class.getResource(fxml));
            page = loader.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }


    /* 主函数 */
    public static void main(String[] args)  {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            ControllerUtils.showAlert("[错误] 初始化数据库驱动失败!");
            System.out.println("ERROR::CLASS::INIT");
        }
        launch(args);
    }
}
