package LibraryManagementSystem;

import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ControllerUtils {
    public static void extractRentBookData(ObservableList<BookInfo> rentBookData, TableView<BookInfo> rentTableField, int id) {
        Statement stmt;
        ResultSet rset;
        try {
            stmt = Main.conn.createStatement();
            rset = stmt.executeQuery(
                    "select * from user_account natural join borrow natural join book where user_id = " + Integer.toString(id));
            while(rset.next()) {
                rentBookData.add(new BookInfo(
                        rset.getString("book_name"),
                        rset.getString("author"),
                        rset.getString("press"),
                        rset.getString("pub_date").substring(0, 4),
                        rset.getString("rent_date"),
                        rset.getString("due_date"),
                        rset.getString("book_index")
                ));
            }
            rentTableField.setItems(rentBookData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void search(ObservableList<BookInfo> searchBookData, TabPane tabPane, Tab searchTab, ChoiceBox<String> searchOption, TextField searchField, TableView<BookInfo> searchTableField) {
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
