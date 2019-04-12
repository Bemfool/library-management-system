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
            case 0:  userStateField.setText("【证状态】" + "无效");
            case 1:  userStateField.setText("【证状态】" + "有效");
            default: userStateField.setText("【证状态】" + "系统出错，请联系工作人员");
        }
        switch(type) {
            case 0:  typeField.setText("【读者类型】C（100元6册）"); break;
            case 1:  typeField.setText("【读者类型】B（200元12册）"); break;
            case 2:  typeField.setText("【读者类型】A（500元无限制）"); break;
            default: typeField.setText("【读者类型】系统出错, 请联系工作人员"); break;
        }
        rentCountField.setText("【已借/可借】" + rentNum + "/" + rentMax);

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

    public void enterKey(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER)
            search(null);
    }
}
