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
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class AdminUIController implements Initializable {
    private Main application;

    @FXML private TabPane tabPane;
    @FXML private Tab searchTab;
    @FXML private Tab personalInfoTab;
    @FXML private Tab returnTab;
    @FXML private Tab borrowTab;

    @FXML private Text managerIdField;
    @FXML private Text managerNameField;
    @FXML private Text privField;
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
    @FXML private TextField returnIdField;
    @FXML private TextField borrowIdField;
    @FXML private TextField borrowListField;

    private String managerName = "", priv = "";

    /* 日期格式 */
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


    /* 函数: setApp
     * 用法: registerUI.setApp(this);
     * ----------------------------------------------------------------------------
     * 用于界面切换。
     */

    public void setApp(Main app) { this.application = app; }


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

        /* 初始化管理员个人界面 */
        Statement stmt;
        ResultSet rset;
        if(Main.id != 0) {
            try {
                stmt = Main.conn.createStatement();
                rset = stmt.executeQuery(
                        "select manager_name, manager_priv" +
                                " from manager_account where manager_id = " + Integer.toString(Main.id));
                if (rset.next()) {
                    managerName = rset.getString("manager_name");
                    priv = rset.getString("manager_priv");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            /* 如果是超级管理员模式登陆 */
            managerName = "超级管理员";
            priv = "A";
        }
        managerIdField.setText("【工号】" + String.format("%0" + 5 + "d", Main.id));
        managerNameField.setText("【姓名】" + managerName);
        privField.setText("【权限级别】" + priv);
        welcome.setText("欢迎您，" + managerName);

        /* 初始化借阅查询表格 */
        choiceCol.setCellValueFactory(new PropertyValueFactory<>("choice"));
        rentNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rentAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        rentPressCol.setCellValueFactory(new PropertyValueFactory<>("press"));
        rentPubYearCol.setCellValueFactory(new PropertyValueFactory<>("pubYear"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        rentDateCol.setCellValueFactory(new PropertyValueFactory<>("rentDate"));

        /* 初始化图书查询表格 */
        searchNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        searchAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        searchPressCol.setCellValueFactory(new PropertyValueFactory<>("press"));
        searchPubYearCol.setCellValueFactory(new PropertyValueFactory<>("pubYear"));
        indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));
    }


    /* 函数: search
     * ----------------------------------------------------------------------------
     * 通过搜索选项和搜索内容对数据库内容进行检索，并将查找的结果打印到表格上
     */

    public void search() {
        ControllerUtils.search(searchBookData, tabPane, searchTab, searchOption, searchField, searchTableField);
    }


    /* 函数: enterReturnId
     * ----------------------------------------------------------------------------
     * 搜索该用户的借阅情况，打印到相应的表格上
     */

    public void enterReturnId() {
        /* 打印前先清空原表格 */
        rentBookData.clear();
        ControllerUtils.extractRentBookData(rentBookData, rentTableField, Integer.parseInt(returnIdField.getText()));
    }


    /* 函数: borrowAll
     * ----------------------------------------------------------------------------
     * 借阅指定索书号的书籍，并存入数据库
     */

    public void borrowAll() {
        PreparedStatement pStmt;
        ResultSet rset;
        /* 获取当天日期和到期日期(即借阅日期+7天) */
        Date rentDate = new Date();
        Date dueDate = new Date(rentDate.getTime() + 7*24*60*60*1000);
        String rentDateStr = df.format(rentDate);
        String dueDateStr = df.format(dueDate);

        String[] borrowList = borrowListField.getText().split(";");

        /* 判断要借阅的书籍数是否大于能够借阅的书籍数 */
        try {
            pStmt = Main.conn.prepareStatement(
                    "SELECT rent_num, rent_max " +
                    "FROM user_account " +
                    "WHERE user_id = ?");
            pStmt.setInt(1, Integer.parseInt(borrowIdField.getText()));
            rset = pStmt.executeQuery();
            while(rset.next()) {
                int rentNum = rset.getInt("rent_num");
                int rentMax = rset.getInt("rent_max");
                if(rentNum + borrowList.length > rentMax) {
                    ControllerUtils.showAlert("[错误] 借阅的书籍数目大于能够借阅的数量!");
                    System.err.println("ERROR::BORROW::NUM");
                    return;
                }
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            ControllerUtils.showAlert("[错误] 读取能够借阅的最大书籍数失败!");
            System.err.println("ERROR::BORROW::SELECT_NUM::FAILED");
            return;
        }

        /* 插入数据库，并设置相应的属性 */
        try {
            pStmt = Main.conn.prepareStatement(
                    "INSERT INTO borrow (book_index, user_id, rent_date, due_date) VALUES (?, ?, ?, ?);" +
                            "UPDATE book SET book_num = book_num - 1 WHERE book_index = ?;" +
                            "UPDATE user_account SET rent_num = rent_num + 1 WHERE user_id = ?"
            );
            pStmt.setInt(2, Integer.parseInt(borrowIdField.getText()));
            pStmt.setInt(6, Integer.parseInt(borrowIdField.getText()));
            pStmt.setString(3, rentDateStr);
            pStmt.setString(4, dueDateStr);
        } catch (SQLException e) {
            System.err.println("ERROR::PSTMT::CREATION");
            return;
        }

        for (String aBorrowList : borrowList) {
            if (!aBorrowList.isEmpty()) {
                try {
                    pStmt.setString(1, aBorrowList);
                    pStmt.setString(5, aBorrowList);
                    pStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("ERROR::PSTMT::SET_VALUE");
                }
            }

        }
    }


    /* 函数: returnSelected
     * ----------------------------------------------------------------------------
     * 归还选中的图书, 需要更新数据库
     */

    public void returnSelected() {
        PreparedStatement pStmt;
        try {
            pStmt = Main.conn.prepareStatement(
                    "DELETE FROM borrow WHERE book_index = ?;" +
                            " UPDATE book SET book_num = book_num + 1 WHERE book_index = ?;" +
                            " UPDATE user_account SET rent_num = rent_num - 1 WHERE user_id = ?;"
            );
            pStmt.setInt(3, Integer.parseInt(returnIdField.getText()));
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        for (BookInfo aRentBookData : rentBookData) {
            if (aRentBookData.getChoice().isSelected()) {
                try {
                    pStmt.setString(1, aRentBookData.getIndex());
                    pStmt.setString(2, aRentBookData.getIndex());
                    pStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        /* 还书后需要刷新借阅界面 */
        enterReturnId();
    }


    /* 函数: register
     * ----------------------------------------------------------------------------
     * 打开注册界面
     */

    public void register() {
        if(priv.equals("A") || priv.equals("B"))
           application.displayRegisterUI();
        else {
            ControllerUtils.showAlert("[错误] 您没有创建用户的权力!");
            System.err.println("ERROR::REGISTER::PRIVILEGE::FAILED");
        }
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
            Main.conn.close();
        } catch (SQLException e) {
            ControllerUtils.showAlert("[错误] 管理员注销失败!");
            System.err.println("ERROR::EXIT::FAILED");
            return;
        }
        application.gotoLoginUI();
    }


    /* 函数: gotoBorrow
     * ----------------------------------------------------------------------------
     * 跳转到借书板块
     */

    public void gotoBorrow(ActionEvent actionEvent) {
        tabPane.getSelectionModel().select(borrowTab);
    }


    /* 函数: gotoReturn
     * ----------------------------------------------------------------------------
     * 跳转到还书板块
     */

    public void gotoReturn(ActionEvent actionEvent) {
        tabPane.getSelectionModel().select(returnTab);
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
}
