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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginUIController implements Initializable {
    private Main application;
    @FXML private ToggleGroup privCheck;
    @FXML private PasswordField passwordField;
    @FXML private TextField userField;

    public void loginButton(ActionEvent actionEvent) throws Exception {
        // 选择的按钮，`普通用户`或`管理员`
        RadioButton selectedBtn = (RadioButton)privCheck.getSelectedToggle();

        // 判断账号密码不为空
        if(userField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            ControllerUtils.showAlert("[错误] 账户名或密码为空!");
            System.out.println("ERROR::USER_ID||PASSWORD::EMPTY");
            return;
        }

        // 通过长度进行判断账户名是否输入错误
        if(userField.getText().length() != 10 && selectedBtn.getText().equals("普通用户")) {
            ControllerUtils.showAlert("[错误] 用户账户名长度不对!");
            System.out.println("ERROR::USER_ID::LENGTH");
            return;
        } else if(userField.getText().length() != 5 && selectedBtn.getText().equals("管理员")) {
            ControllerUtils.showAlert("[错误] 管理员账户名长度不对!");
            System.out.println("ERROR::MANAGER_ID::LENGTH");
            return;
        }

        // 对输入的密码进行MD5加密
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(passwordField.getText().getBytes(), 0, passwordField.getText().length());
        String md5Password = new BigInteger(1, md5.digest()).toString(16);

        PreparedStatement pStmt;
        ResultSet rset;
        if(selectedBtn.getText().equals("普通用户")) {
            // 判断密码正确性，正确则跳转普通用户界面
            pStmt = Main.conn.prepareStatement("SELECT password FROM user_account WHERE user_id = ?");
            pStmt.setInt(1, Integer.parseInt(userField.getText()));
            rset = pStmt.executeQuery();
            if(rset.next())
                if(md5Password.equals(rset.getString("password"))) {
                    Main.id = Integer.parseInt(userField.getText());
                    application.gotoUserUI();
                } else {

                    System.out.println("ERROR::PASSWORD::WRONG");
                }
            else
                // TODO 完善输错账号时候的反馈
                System.out.println("Wrong user id");
        } else {
            // 跳转管理员界面
            pStmt = Main.conn.prepareStatement("SELECT password FROM manager_account WHERE manager_id = ?");
            pStmt.setInt(1, Integer.parseInt(userField.getText()));
            rset = pStmt.executeQuery();
            if(rset.next())
                if(md5Password.equals(rset.getString("password"))) {
                    Main.id = Integer.parseInt(userField.getText());
                    application.gotoAdminUI();
                } else
                    System.out.println("Wrong password" + md5Password + " " + rset.getString("password"));
            else
                System.out.println("Wrong user id");
        }

    }
    public void register(ActionEvent mouseEvent) {
        // 进入注册账户界面
        // TODO
        System.out.println("register");
    }

    public void forgetPassword(ActionEvent mouseEvent) {
        // 进入找回密码界面
        // TODO
        System.out.println("forget password.");
    }

    public void setApp(Main app) {
        this.application = app;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
    }

    public void enterKey(KeyEvent keyEvent) throws Exception {
        if(keyEvent.getCode() == KeyCode.ENTER)
            loginButton(null);
    }
}
