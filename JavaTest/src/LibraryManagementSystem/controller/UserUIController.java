package LibraryManagementSystem.controller;

import LibraryManagementSystem.Main;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class UserUIController  implements Initializable {
    private Main application;
    @FXML private ChoiceBox searchOption;
    @FXML private Text userIdField;
    @FXML private Text userNameField;
    @FXML private Text userStateField;
    @FXML private Text typeField;
    @FXML private Text rentCountField;
    @FXML private Text welcome;

    private String userName = "";
    private int rentNum = -1, rentMax = -1, userState = -1, type = -1;

    public void setApp(Main app) {
        this.application = app;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        // 初始化ChoiceBox
        searchOption.setItems(FXCollections.observableArrayList(
                "书名", "作者", "出版社", "出版日期"
        ));
        searchOption.setValue("书名");

        // 初始化个人界面

        Statement stmt;
        ResultSet rset;
        System.out.println(Main.id);
        try {
            stmt = Main.conn.createStatement();
            rset = stmt.executeQuery(
                    "select user_name, state, reader_type, rent_num, rent_max" +
                            " from user_account where user_id = " + Integer.toString(Main.id));
            if(rset.next()) {
                userName = rset.getString("user_name");
                userState = rset.getInt("state");
                type = rset.getInt("reader_type");
                rentNum = rset.getInt("rent_num");
                rentMax = rset.getInt("rent_max");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        userIdField.setText("【读者证号】" + String.format("%0" + 10 + "d", Main.id));
        welcome.setText("欢迎您，" + userName);
        userNameField.setText("【读者姓名】" + userName);
        switch(userState) {
            case 0: userStateField.setText("【证状态】" + "无效");
            case 1: userStateField.setText("【证状态】" + "有效");
            default:  userStateField.setText("【证状态】" + "系统出错，请联系工作人员");
        }
        switch(type) {
            case 0: typeField.setText("【读者类型】C（100元6册）"); break;
            case 1: typeField.setText("【读者类型】B（200元12册）"); break;
            case 2: typeField.setText("【读者类型】A（500元无限制）"); break;
            default: typeField.setText("【读者类型】系统出错, 请联系工作人员"); break;
        }
        rentCountField.setText("【已借/可借】" + rentNum + "/" + rentMax);
    }




}
