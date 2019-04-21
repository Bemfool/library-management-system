package LibraryManagementSystem.controller;

import LibraryManagementSystem.BookInfo;
import LibraryManagementSystem.Main;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ControllerUtils {
    static void extractRentBookData(ObservableList<BookInfo> rentBookData, TableView<BookInfo> rentTableField, int id) {
        Statement stmt;
        ResultSet rset;
        try {
            stmt = Main.conn.createStatement();
            rset = stmt.executeQuery(
                    "SELECT * FROM user_account NATURAL JOIN borrow " +
                            "NATURAL JOIN book " +
                            "WHERE user_id = " + Integer.toString(id));
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

    static void search(ObservableList<BookInfo> searchBookData, TabPane tabPane, Tab searchTab, ChoiceBox<String> searchOption, TextField searchField, TableView<BookInfo> searchTableField) {
        searchBookData.clear();
        tabPane.getSelectionModel().select(searchTab);
        PreparedStatement pStmt;
        ResultSet rset;
        try {
            switch (searchOption.getValue()) {
                case "书名":
                    pStmt = Main.conn.prepareStatement("SELECT * FROM book WHERE book_name LIKE ?");
                    break;
                case "作者":
                    pStmt = Main.conn.prepareStatement("SELECT * FROM book WHERE author LIKE ?");
                    break;
                case "出版社":
                    pStmt = Main.conn.prepareStatement("SELECT * FROM book WHERE press LIKE ?");
                    break;
                case "出版日期":
                    pStmt = Main.conn.prepareStatement("SELECT * FROM book WHERE pub_date LIKE ?");
                    break;
                default:
                    System.out.println("ERROR::CHOICE_BOX");
                    return;
            }
            String strToSearch = searchField.getText();
            StringBuilder strVague = new StringBuilder("%");
            for(int i=0; i<strToSearch.length(); i++) {
                strVague.append(strToSearch.charAt(i));
                strVague.append("%");
            }
            pStmt.setString(1, String.valueOf(strVague));
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

    public static void showAlert(String message) {
        Stage window = new Stage();
        window.setAlwaysOnTop(true);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setResizable(false);
        window.setMinWidth(300);
        window.setMinHeight(150);
        Button button = new Button("确定");
        button.setOnAction(e -> window.close());
        Label label = new Label(message);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, button);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

}
