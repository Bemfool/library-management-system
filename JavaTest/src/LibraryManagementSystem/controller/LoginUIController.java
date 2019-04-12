package LibraryManagementSystem.controller;

import LibraryManagementSystem.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

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
        // TODO 可以完善账号或密码为空时候的反馈
        if(userField.getText().isEmpty() || passwordField.getText().isEmpty())
            return;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(passwordField.getText().getBytes(), 0, passwordField.getText().length());
        String md5Password = new BigInteger(1, md5.digest()).toString(16);
        PreparedStatement pStmt;
        ResultSet rset;
        RadioButton selectedBtn = (RadioButton)privCheck.getSelectedToggle();
        if(selectedBtn.getText().equals("普通用户")) {
            // 跳转普通用户界面
            pStmt = Main.conn.prepareStatement("select password from user_account where user_id = ?");
            pStmt.setInt(1, Integer.parseInt(userField.getText()));
            rset = pStmt.executeQuery();
            if(rset.next())
                if(md5Password.equals(rset.getString("password"))) {
                    Main.id = Integer.parseInt(userField.getText());
                    application.gotoUserUI();
                } else
                    // TODO 完善输错密码时候的反馈
                    System.out.println("Wrong password");
            else
                // TODO 完善输错账号时候的反馈
                System.out.println("Wrong user id");
        } else {
            // 跳转管理员界面
            pStmt = Main.conn.prepareStatement("select password from manager_account where manager_id = ?");
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

}
