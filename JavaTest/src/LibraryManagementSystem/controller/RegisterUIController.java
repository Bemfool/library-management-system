package LibraryManagementSystem.controller;

import LibraryManagementSystem.Main;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegisterUIController implements Initializable {
    private Main application;
    @FXML private TextField nameField;
    @FXML private TextField idField;
    @FXML private TextField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ChoiceBox<String> typeField;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        typeField.setItems(FXCollections.observableArrayList(
                "D(仅查询)", "C(100元6册)", "B(200元12册)", "A(无限制)"
        ));
        typeField.setValue("D(仅查询)");
    }
    public void setApp(Main app) {
        this.application = app;
    }

    public void register(ActionEvent actionEvent) {
        PreparedStatement pStmt;
        if(!nameField.getText().equals("") && !idField.getText().equals("") && !passwordField.getText().equals("")) {
            try {
                pStmt = Main.conn.prepareStatement(
                        "INSERT INTO user_account " +
                                "(user_name, id_no, email_address, phone_no, reader_type)" +
                                "VALUES (?, ?, ?, ?, ?)");
                pStmt.setString(1, nameField.getText());
                pStmt.setString(2, idField.getText());
                pStmt.setString(3, emailField.getText());
                pStmt.setString(4, phoneField.getText());
                switch (typeField.getValue()) {
                    case "D(仅查询)":
                        pStmt.setInt(5, 0);
                        break;
                    case "C(100元6册)":
                        pStmt.setInt(5, 1);
                        break;
                    case "B(200元12册)":
                        pStmt.setInt(5, 2);
                        break;
                    case "A(无限制)":
                        pStmt.setInt(5, 3);
                        break;
                    default:
                        ControllerUtils.showAlert("[错误] 读取读者类型出错!");
                        System.out.println("ERROR::READ_TYPE");
                        return;
                }
                pStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                ControllerUtils.showAlert("[错误] 插入新用户错误!");
                System.out.println("ERROR::CREATE_USER::INSERT::FAILED");
                return;
            }
            ResultSet rset;
            String newId = "";
            try {
                pStmt = Main.conn.prepareStatement("SELECT user_id FROM user_account WHERE id_no = ?");
                pStmt.setString(1, idField.getText());
                rset = pStmt.executeQuery();
                while(rset.next())
                    newId = String.format("%0" + 10 + "d", rset.getInt("user_id"));
            } catch (SQLException e) {
                e.printStackTrace();
                ControllerUtils.showAlert("[错误] 检索新用户账号错误!");
                System.out.println("ERROR::CREATE_USER::INSERT::FAILED");
                return;
            }
            try {
                pStmt = Main.conn.prepareStatement("CREATE USER ?@'%' IDENTIFIED BY ?");
                pStmt.setString(1, newId);
                pStmt.setString(2, passwordField.getText());
                pStmt.execute();
                pStmt = Main.conn.prepareStatement("GRANT SELECT ON library.* TO ?@'%'");
                pStmt.setString(1, newId);
                pStmt.execute();
                Main.conn.prepareStatement("GRANT UPDATE ON libray.borrow TO ?@'%'");
                pStmt.setString(1, newId);
                pStmt.execute();
                ControllerUtils.showAlert("[成功] 请记住您的账号: " + newId);
            } catch (SQLException e) {
                e.printStackTrace();
                ControllerUtils.showAlert("[错误] 创建新用户错误!");
                System.out.println("ERROR::CREATE_USER::CREATE::FAILED");
            }
        } else {
            ControllerUtils.showAlert("[错误] 姓名/身份号/密码不能为空");
            System.out.println("ERROR::CREATE_USER::EMPTY::FAILED");
        }
    }

    public void enterKey(KeyEvent keyEvent) {
    }
}
