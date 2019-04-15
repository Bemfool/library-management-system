package LibraryManagementSystem.controller;

import LibraryManagementSystem.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
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

    public void loginButton(){
        /* 选择的按钮，`普通用户`或`管理员` */
        RadioButton selectedBtn = (RadioButton)privCheck.getSelectedToggle();

        /* 判断账号密码不能为空 */
        if(userField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            ControllerUtils.showAlert("[错误] 账户名或密码为空!");
            System.err.println("ERROR::USER_ID||PASSWORD::EMPTY");
            return;
        }

        /* 通过长度进行判断账户名是否输入错误
         * 注：旧版设计时候的判断，当前版本不是必须的
         */
        if(!userField.getText().equals("root")) {
            if(userField.getText().length() != 10 && selectedBtn.getText().equals("普通用户")) {
                ControllerUtils.showAlert("[错误] 用户账户名长度不对!");
                System.err.println("ERROR::USER_ID::LENGTH");
                return;
            } else if(userField.getText().length() != 5 && selectedBtn.getText().equals("管理员")) {
                ControllerUtils.showAlert("[错误] 管理员账户名长度不对!");
                System.err.println("ERROR::MANAGER_ID::LENGTH");
                return;
            }
        }

        /* 使用该账号密码尝试连接数据库 */
        try {
            Main.conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Library" +
                            "?serverTimezone=GMT" +
                            "&useSSL=false" +
                            "&allowMultiQueries=true" +
                            "&allowPublicKeyRetrieval=true",
                    userField.getText(), passwordField.getText());
        } catch (SQLException e) {
            ControllerUtils.showAlert("[错误] 账号或密码错误!");
            System.err.println("ERROR::CONNECTION::FAILED");
            return;
        }

        PreparedStatement pStmt;
        ResultSet rset;
        if(selectedBtn.getText().equals("普通用户")) {
            /* root账户直接进入用户界面 */
            if(userField.getText().equals("root")) {
                try {
                    /* 如果是root模式则会跳出警告 */
                    ControllerUtils.showAlert("[警告] 正在使用root模式登陆!");
                    System.err.println("WARMING::ROOT");
                    application.gotoUserUI();
                } catch (Exception e) {
                    ControllerUtils.showAlert("[错误] root用户无法跳转到用户主界面!");
                    System.err.println("ERROR::ROOT::GOTO_USER_UI::FAILED");
                }
                return;
            }

            /* 查找该用户是否存在 */
            try {
                pStmt = Main.conn.prepareStatement("SELECT * FROM user_account WHERE user_id = ?");
                pStmt.setInt(1, Integer.parseInt(userField.getText()));
                rset = pStmt.executeQuery();
                if(rset.next()) {
                    Main.id = Integer.parseInt(userField.getText());
                    application.gotoUserUI();
                } else {
                    /* 注: 此处本来是为了避免管理员账户用于登陆普通用户模式
                     * 但是由于前方有长度判断，因此也不存在这样的可能，
                     * 此处判断略显多余
                     */
                    ControllerUtils.showAlert("[错误] 该用户不存在!");
                    System.err.println("ERROR::USER_ID::NOT_FOUND");
                }
            } catch (SQLException e) {
                ControllerUtils.showAlert("[错误] 数据库指令错误!");
                System.err.println("ERROR::USER_ID::SELECT::FAILED");
            } catch (Exception e) {
                ControllerUtils.showAlert("[错误] 无法跳转到用户主界面!");
                System.err.println("ERROR::GOTO_USER_UI::FAILED");
            }
        } else {
            /* root账户直接进入管理员界面 */
            if(userField.getText().equals("root")) {
                try {
                    ControllerUtils.showAlert("[警告] 正在使用root模式登陆!");
                    System.err.println("WARMING::ROOT");
                    application.gotoAdminUI();
                } catch (Exception e) {
                    ControllerUtils.showAlert("[错误] 无法跳转到管理员主界面!");
                    System.err.println("ERROR::GOTO_MANAGER_UI::FAILED");
                }
                return;
            }
            /* 查找管理员是否存在 */
            try {
                pStmt = Main.conn.prepareStatement("SELECT * FROM manager_account WHERE manager_id = ?");
                pStmt.setInt(1, Integer.parseInt(userField.getText()));
                rset = pStmt.executeQuery();
                if(rset.next()) {
                    Main.id = Integer.parseInt(userField.getText());
                    application.gotoAdminUI();
                } else {
                    /* 注: 此处本来是为了避免用户账户用于登陆管理员模式
                     * 但是由于前方有长度判断，因此也不存在这样的可能，
                     * 此处判断略显多余
                     */
                    ControllerUtils.showAlert("[错误] 该管理员不存在!");
                    System.err.println("ERROR::MANAGER_ID::NOT_FOUND");
                }
            } catch (SQLException e) {
                ControllerUtils.showAlert("[错误] 数据库指令错误!");
                System.err.println("ERROR::MANAGER_ID::SELECT::FAILED");
            } catch (Exception e) {
                ControllerUtils.showAlert("[错误] 无法跳转到管理员主界面!");
                System.err.println("ERROR::GOTO_ADMIN_UI::FAILED");
            }
        }
    }


    /* 函数: setApp
     * 用法: registerUI.setApp(this);
     * ----------------------------------------------------------------------------
     * 用于界面切换。
     */

    public void setApp(Main app) { this.application = app; }


    /* 函数: initialize
     * ----------------------------------------------------------------------------
     * 界面初始化(不需要)。
     */

    @Override
    public void initialize(URL url, ResourceBundle rb){ }


    /* 函数: enterKey
     * ----------------------------------------------------------------------------
     * 在搜索栏回车可以直接搜索。
     */

    public void enterKey(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER)
            loginButton();
    }
}
