package LibraryManagementSystem.controller;

import LibraryManagementSystem.BookInfo;
import LibraryManagementSystem.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class UserUIController  implements Initializable {
    private Main application;

    @FXML private TabPane tabPane;
    @FXML private Tab searchTab;
    @FXML private Tab personalInfoTab;
    @FXML private Tab borrowSearchTab;

    @FXML private Text userIdField;
    @FXML private Text userNameField;
    @FXML private Text userStateField;
    @FXML private Text typeField;
    @FXML private Text rentCountField;
    @FXML private Text welcome;
    @FXML private TableView<BookInfo> rentTableField;
    @FXML private TableColumn<BookInfo, CheckBox> choiceCol;
    @FXML private TableColumn<BookInfo, String> rentNameCol;
    @FXML private TableColumn<BookInfo, String> rentAuthorCol;
    @FXML private TableColumn<BookInfo, String> rentPressCol;
    @FXML private TableColumn<BookInfo, String> rentPubYearCol;
    @FXML private TableColumn<BookInfo, String> rentDateCol;
    @FXML private TableColumn<BookInfo, String> dueDateCol;
    private ObservableList<BookInfo> rentBookData = FXCollections.observableArrayList();
    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> searchOption;
    @FXML private TableView<BookInfo> searchTableField;
    @FXML private TableColumn<BookInfo, String> searchNameCol;
    @FXML private TableColumn<BookInfo, String> searchAuthorCol;
    @FXML private TableColumn<BookInfo, String> searchPressCol;
    @FXML private TableColumn<BookInfo, String> searchPubYearCol;
    @FXML private TableColumn<BookInfo, String> indexCol;
    @FXML private TableColumn<BookInfo, Integer> numCol;
    private ObservableList<BookInfo> searchBookData = FXCollections.observableArrayList();

    private String userName = "";
    private int rentNum = -1, rentMax = -1, userState = -1, type = -1;


    /* 函数: setApp
     * 用法: registerUI.setApp(this);
     * ----------------------------------------------------------------------------
     * 用于界面切换。
     */

    public void setApp(Main app) {
        this.application = app;
    }


    /* 函数: initialize
     * ----------------------------------------------------------------------------
     * 界面初始化。
     */

    @Override
    public void initialize(URL url, ResourceBundle rb){
        /* 初始化选择框 */
        searchOption.setItems(FXCollections.observableArrayList(
                "书名", "作者", "出版社", "出版日期"));
        searchOption.setValue("书名");

        /* 初始化个人界面 */
        Statement stmt;
        ResultSet rset;
        if(Main.id != 0) {
            try {
                /* 从数据库中读取用户信息 */
                stmt = Main.conn.createStatement();
                rset = stmt.executeQuery(
                        "SELECT user_name, state, reader_type, rent_num, rent_max" +
                                " FROM user_account WHERE user_id = " + Integer.toString(Main.id));
                if (rset.next()) {
                    userName = rset.getString("user_name");
                    userState = rset.getInt("state");
                    type = rset.getInt("reader_type");
                    rentNum = rset.getInt("rent_num");
                    rentMax = rset.getInt("rent_max");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            /* 如果是超级管理员模式登陆 */
            userName = "超级管理员";
            userState = 1;
            type = 4;
        }

        /* 将用户信息打印到界面上 */
        userIdField.setText("【读者证号】" + String.format("%0" + 10 + "d", Main.id));
        welcome.setText("欢迎您，" + userName);
        userNameField.setText("【读者姓名】" + userName);
        switch(userState) {
            case 0:  userStateField.setText("【证状态】" + "无效"); break;
            case 1:  userStateField.setText("【证状态】" + "有效"); break;
            default: userStateField.setText("【证状态】" + "系统出错，请联系工作人员");
        }
        switch(type) {
            case 0:  typeField.setText("【读者类型】D（仅查询）"); break;
            case 1:  typeField.setText("【读者类型】C（100元6册）"); break;
            case 2:  typeField.setText("【读者类型】B（200元12册）"); break;
            case 3:  typeField.setText("【读者类型】A（500元无限制）"); break;
            case 4:  typeField.setText("【读者类型】超级管理者模式"); break;
            default: typeField.setText("【读者类型】系统出错, 请联系工作人员");
        }
        if(Main.id != 0)
            rentCountField.setText("【已借/可借】" + rentNum + "/" + rentMax);
        else
            rentCountField.setText("【已借/可借】0/Infinity");

        /* 初始化借阅查询的表格 */
        choiceCol.setCellValueFactory(new PropertyValueFactory<>("choice"));
        rentNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rentAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        rentPressCol.setCellValueFactory(new PropertyValueFactory<>("press"));
        rentPubYearCol.setCellValueFactory(new PropertyValueFactory<>("pubYear"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        rentDateCol.setCellValueFactory(new PropertyValueFactory<>("rentDate"));
        ControllerUtils.extractRentBookData(rentBookData, rentTableField, Main.id);

        /* 初始化图书查询的表格 */
        searchNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        searchAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        searchPressCol.setCellValueFactory(new PropertyValueFactory<>("press"));
        searchPubYearCol.setCellValueFactory(new PropertyValueFactory<>("pubYear"));
        indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));
    }


    /* 函数: search
     * ----------------------------------------------------------------------------
     * 通过搜索选项和搜索内容对数据库内容进行检索，并将查找的结果打印到表格上。
     */

    public void search() {
        ControllerUtils.search(searchBookData, tabPane, searchTab, searchOption, searchField, searchTableField);
    }


    /* 函数: enterKey
     * ----------------------------------------------------------------------------
     * 在搜索栏回车可以直接搜索。
     */

    public void enterKey(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER)
            search();
    }


    /* 函数: gotoPersonalInfo
     * ----------------------------------------------------------------------------
     * 跳转到个人信息板块
     */

    public void gotoPersonalInfo(ActionEvent actionEvent) {
        tabPane.getSelectionModel().select(personalInfoTab);
    }


    /* 函数: exit
     * ----------------------------------------------------------------------------
     * 注销账户，回到登陆界面
     */

    public void exit(ActionEvent actionEvent) {
        try {
            Main.id = 0;
            Main.conn.close();
        } catch (SQLException e) {
            ControllerUtils.showAlert("[错误] 用户注销失败!");
            System.err.println("ERROR::EXIT::FAILED");
            return;
        }
        application.gotoLoginUI();
    }


    /* 函数: gotoBorrowSearch
     * ----------------------------------------------------------------------------
     * 跳转到借阅查询板块
     */

    public void gotoBorrowSearch(ActionEvent actionEvent) {
        tabPane.getSelectionModel().select(borrowSearchTab);
    }


    /* 函数: about
     * ----------------------------------------------------------------------------
     * 打开软件相关信息，包括作者姓名和联系邮箱
     */

    public void about(ActionEvent actionEvent) {
        ControllerUtils.showAlert(
                "\n图书管理系统 v2.0\n" +
                        "\n作者: 林逸竹 " +
                        "\n联系邮箱: 897735626@qq.com\n\n");
    }


    /* 函数: renewAll
     * ----------------------------------------------------------------------------
     * 续借所有书籍，每次续借增加7天期限。
     */

    public void renewAll(ActionEvent actionEvent) {
        try {
            PreparedStatement pStmt = Main.conn.prepareStatement(
                    "UPDATE library.borrow " +
                            "SET due_date = DATE_ADD(due_date, INTERVAL 7 DAY) " +
                            "WHERE user_id = ?");
            pStmt.setInt(1, Main.id);
            pStmt.executeUpdate();
            ControllerUtils.showAlert("[成功] 续借所有书籍成功!!");
            System.out.println("SUCCESS::RENEW_ALL");
        } catch (SQLException e) {
//            e.printStackTrace();
            ControllerUtils.showAlert("[错误] 续借所有书籍失败!\n" +
                    "请联系相关工作人员进行检查");
            System.err.println("ERROR::RENEW_ALL::FAILED");
            return;
        }
        rentBookData.clear();
        ControllerUtils.extractRentBookData(rentBookData, rentTableField, Main.id);
    }


    /* 函数: renewSelected
     * ----------------------------------------------------------------------------
     * 续借选定的书籍，每次续借增加7天期限。
     */

    public void renewSelected(ActionEvent actionEvent) {
        try {
            PreparedStatement pStmt = Main.conn.prepareStatement(
                    "UPDATE library.borrow " +
                            "SET due_date = DATE_ADD(due_date, INTERVAL 7 DAY) " +
                            "WHERE user_id = ? AND book_index = ?");
            pStmt.setInt(1, Main.id);
            for (BookInfo aRentBookData : rentBookData) {
                if (aRentBookData.getChoice().isSelected()) {
                    pStmt.setString(2, aRentBookData.getIndex());
                    pStmt.executeUpdate();
                }
            }
            ControllerUtils.showAlert("[成功] 续借选择的书籍成功!!");
            System.out.println("SUCCESS::RENEW_SELECTED");
        } catch (SQLException e) {
//            e.printStackTrace();
            ControllerUtils.showAlert("[错误] 续借选择的书籍失败!\n" +
                    "请联系相关工作人员进行检查");
            System.err.println("ERROR::RENEW_SELECTED::FAILED");
            return;
        }
        rentBookData.clear();
        ControllerUtils.extractRentBookData(rentBookData, rentTableField, Main.id);
    }
}
