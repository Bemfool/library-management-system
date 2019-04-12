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

    private String managerName = "", priv = "";

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
        try {
            stmt = Main.conn.createStatement();
            rset = stmt.executeQuery(
                    "select * from user_account natural join borrow natural join book where user_id = " + Integer.toString(Main.id));
            while(rset.next()) {
                rentBookData.add(new BookInfo(
                        rset.getString("book_name"),
                        rset.getString("author"),
                        rset.getString("press"),
                        rset.getString("pub_date").substring(0, 4),
                        rset.getString("rent_date"),
                        rset.getString("due_date")
                ));
            }
            rentTableField.setItems(rentBookData);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // 初始化图书查询
        searchNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        searchAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        searchPressCol.setCellValueFactory(new PropertyValueFactory<>("press"));
        searchPubYearCol.setCellValueFactory(new PropertyValueFactory<>("pubYear"));
        indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));

    }

    public void search(ActionEvent actionEvent) {
        searchBookData.clear();
        tabPane.getSelectionModel().select(searchTab);
        PreparedStatement pStmt;
        ResultSet rset = null;
        try {
            if (searchOption.getValue().equals("书名"))
                pStmt = Main.conn.prepareStatement("select * from book where book_name like ?");
            else if(searchOption.getValue().equals("作者"))
                pStmt = Main.conn.prepareStatement("select * from book where author like ?");
            else if(searchOption.getValue().equals("出版社"))
                pStmt = Main.conn.prepareStatement("select * from book where press like ?");
            else if(searchOption.getValue().equals("出版日期"))
                pStmt = Main.conn.prepareStatement("select * from book where pub_date like ?");
            else {
                System.out.println("ERROR::CHOICE_BOX");
                return;
            }
            pStmt.setString(1, "%" + searchField.getText() + "%");
            rset = pStmt.executeQuery();
            while(rset.next()) {
                searchBookData.add(new BookInfo(
                        rset.getString("book_name"),
                        rset.getString("author"),
                        rset.getString("press"),
                        rset.getString("pub_date").substring(0, 4),
                        rset.getString("book_index"),
                        rset.getInt("book_num"))
                );
            }
            searchTableField.setItems(searchBookData);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
