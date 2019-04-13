package LibraryManagementSystem.controller;

import LibraryManagementSystem.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginUIController implements Initializable {
    private Main application;
    @FXML private ToggleGroup privCheck;
    @FXML private PasswordField passwordField;
    @FXML private TextField userField;

    public void loginButton(ActionEvent actionEvent){
        // 选择的按钮，`普通用户`或`管理员`
        RadioButton selectedBtn = (RadioButton)privCheck.getSelectedToggle();

        // 判断账号密码不为空
        if(userField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            ControllerUtils.showAlert("[错误] 账户名或密码为空!");
            System.out.println("ERROR::USER_ID||PASSWORD::EMPTY");
            return;
        }

        // 通过长度进行判断账户名是否输入错误
        if(!userField.getText().equals("root")) {
            if(userField.getText().length() != 10 && selectedBtn.getText().equals("普通用户")) {
                ControllerUtils.showAlert("[错误] 用户账户名长度不对!");
                System.out.println("ERROR::USER_ID::LENGTH");
                return;
            } else if(userField.getText().length() != 5 && selectedBtn.getText().equals("管理员")) {
                ControllerUtils.showAlert("[错误] 管理员账户名长度不对!");
                System.out.println("ERROR::MANAGER_ID::LENGTH");
                return;
            }
        }

        // 尝试连接数据库
        try {
            Main.conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Library" +
                            "?serverTimezone=GMT" +
                            "&useSSL=false" +
                            "&allowMultiQueries=true" +
                            "&allowPublicKeyRetrieval=true",
                    userField.getText(), passwordField.getText());
        } catch (SQLException e) {
            e.printStackTrace();
            ControllerUtils.showAlert("[错误] 账号或密码错误!");
            System.out.println("ERROR::CONNECTION::FAILED");
        }

        PreparedStatement pStmt;
        ResultSet rset;
        if(selectedBtn.getText().equals("普通用户")) {
            // root账户直接进入用户界面
            if(userField.getText().equals("root")) {
                try {
                    ControllerUtils.showAlert("[警告] 正在使用root模式登陆!");
                    System.out.println("WARMING::ROOT");
                    application.gotoUserUI();
                } catch (Exception e) {
                    e.printStackTrace();
                    ControllerUtils.showAlert("[错误] 无法跳转到用户主界面!");
                    System.out.println("ERROR::GOTO_USER_UI::FAILED");
                }
                return;
            }
            // 判断密码正确性，正确则跳转普通用户界面
            try {
                pStmt = Main.conn.prepareStatement("SELECT * FROM user_account WHERE user_id = ?");
                pStmt.setInt(1, Integer.parseInt(userField.getText()));
                rset = pStmt.executeQuery();
                if(rset.next()) {
                    Main.id = Integer.parseInt(userField.getText());
                    application.gotoUserUI();
                } else {
                    ControllerUtils.showAlert("[错误] 该用户不存在!");
                    System.out.println("ERROR::USER_ID::NOT_FOUND");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ControllerUtils.showAlert("[错误] 数据库指令错误!");
                System.out.println("ERROR::USER_ID::SELECT::FAILED");
            } catch (Exception e) {
                e.printStackTrace();
                ControllerUtils.showAlert("[错误] 无法跳转到用户主界面!");
                System.out.println("ERROR::GOTO_USER_UI::FAILED");
            }
        } else {
            // root账户直接进入管理员界面
            if(userField.getText().equals("root")) {
                try {
                    ControllerUtils.showAlert("[警告] 正在使用root模式登陆!");
                    System.out.println("WARMING::ROOT");
                    application.gotoAdminUI();
                } catch (Exception e) {
                    e.printStackTrace();
                    ControllerUtils.showAlert("[错误] 无法跳转到管理员主界面!");
                    System.out.println("ERROR::GOTO_MANAGER_UI::FAILED");
                }
                return;
            }
            // 判断密码正确性，正确则跳转管理员界面
            try {
                pStmt = Main.conn.prepareStatement("SELECT * FROM manager_account WHERE manager_id = ?");
                pStmt.setInt(1, Integer.parseInt(userField.getText()));
                rset = pStmt.executeQuery();
                if(rset.next()) {
                    Main.id = Integer.parseInt(userField.getText());
                    application.gotoAdminUI();
                } else {
                    ControllerUtils.showAlert("[错误] 该管理员不存在!");
                    System.out.println("ERROR::MANAGER_ID::NOT_FOUND");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ControllerUtils.showAlert("[错误] 数据库指令错误!");
                System.out.println("ERROR::MANAGER_ID::SELECT::FAILED");
            } catch (Exception e) {
                e.printStackTrace();
                ControllerUtils.showAlert("[错误] 无法跳转到管理员主界面!");
                System.out.println("ERROR::GOTO_ADMIN_UI::FAILED");
            }
        }

    }
    public void register(ActionEvent mouseEvent) {
        // 进入注册账户界面
        // TODO
//        RadioButton selectedBtn = (RadioButton)privCheck.getSelectedToggle();
//        PreparedStatement pStmt = Main.conn.prepareStatement("CREATE USER ?@'%' IDENTIFIED BY ?");
//
//        if(selectedBtn.getText().equals("普通用户")) {
//
//        }

    }

    public void forgetPassword(ActionEvent mouseEvent) {
        // 进入找回密码界面
        // TODO
        System.out.println("HINT::FORGET_PASSWORD");
    }

    public void setApp(Main app) { this.application = app; }

    @Override
    public void initialize(URL url, ResourceBundle rb){ }

    public void enterKey(KeyEvent keyEvent) throws Exception {
        if(keyEvent.getCode() == KeyCode.ENTER)
            loginButton(null);
    }
}
