package LibraryManagementSystem.controller;

import LibraryManagementSystem.BookInfo;
import LibraryManagementSystem.ControllerUtils;
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
import java.util.List;
import java.util.ResourceBundle;

public class AdminUIController implements Initializable {
    private Main application;
    @FXML private TabPane tabPane;
    @FXML private Tab searchTab;
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
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

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
        try {
            stmt = Main.conn.createStatement();
            rset = stmt.executeQuery(
                    "select manager_name, manager_priv" +
                            " from manager_account where manager_id = " + Integer.toString(Main.id));
            if(rset.next()) {
                managerName = rset.getString("manager_name");
                priv = rset.getString("manager_priv");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        managerIdField.setText("【工号】" + String.format("%0" + 5 + "d", Main.id));
        managerNameField.setText("【姓名】" + managerName);
        privField.setText("【权限级别】" + priv);
        welcome.setText("欢迎您，" + managerName);

        // 初始化借阅查询
        choiceCol.setCellValueFactory(new PropertyValueFactory<>("choice"));
        rentNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rentAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        rentPressCol.setCellValueFactory(new PropertyValueFactory<>("press"));
        rentPubYearCol.setCellValueFactory(new PropertyValueFactory<>("pubYear"));
        dueDateCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        rentDateCol.setCellValueFactory(new PropertyValueFactory<>("rentDate"));

        // 初始化图书查询
        searchNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        searchAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        searchPressCol.setCellValueFactory(new PropertyValueFactory<>("press"));
        searchPubYearCol.setCellValueFactory(new PropertyValueFactory<>("pubYear"));
        indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));

    }

    public void search(ActionEvent actionEvent) {
        ControllerUtils.search(searchBookData, tabPane, searchTab, searchOption, searchField, searchTableField);

    }

    public void enterReturnId(ActionEvent actionEvent) {
        rentBookData.clear();
        ControllerUtils.extractRentBookData(rentBookData, rentTableField, Integer.parseInt(returnIdField.getText()));
    }

    public void borrowAll(ActionEvent actionEvent) {
        Date rentDate = new Date();
        Date dueDate = new Date(rentDate.getTime() + 7*24*60*60*1000);
        String rentDateStr = df.format(rentDate);
        String dueDateStr = df.format(dueDate);
        PreparedStatement pStmt;

        try {
            pStmt = Main.conn.prepareStatement(
                    "INSERT INTO borrow (book_index, user_id, rent_date, due_date) VALUES (?, ?, ?, ?);" +
                            "UPDATE book SET book_num = book_num - 1; where book_index = ?" +
                            "UPDATE user_account SET rent_num = rent_num + 1 where user_id = ?"
            );
            pStmt.setInt(2, Integer.parseInt(borrowIdField.getText()));
            pStmt.setInt(6, Integer.parseInt(borrowIdField.getText()));
            pStmt.setString(3, rentDateStr);
            pStmt.setString(4, dueDateStr);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ERROR::PSTMT::CREATION");
            return;
        }
        String[] borrowList = borrowListField.getText().split(";");
        for(int i=0; i<borrowList.length; i++) {
            if(borrowList[i].isEmpty())
                continue;
            else {
                try {
                    pStmt.setString(1, borrowList[i]);
                    pStmt.setString(5, borrowList[i]);
                    pStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("ERROR::PSTMT::SET_VALUE");
                }
            }

        }
    }

    public void returnSelected(ActionEvent actionEvent) {
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
        enterReturnId(null);
    }
}
