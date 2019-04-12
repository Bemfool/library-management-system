package LibraryManagementSystem;

import javafx.beans.property.*;
import javafx.scene.control.CheckBox;

public class BookInfo {
    private ObjectProperty<CheckBox> choice;
    private StringProperty name;
    private StringProperty author;
    private StringProperty press;
    private StringProperty pubYear;
    private StringProperty rentDate;
    private StringProperty dueDate;
    private StringProperty index;
    private IntegerProperty num;

    public BookInfo(String name, String author, String press, String pubYear, String rentDate, String dueDate) {
        this.choice = new SimpleObjectProperty<CheckBox>(new CheckBox());
        this.name = new SimpleStringProperty(name);
        this.author = new SimpleStringProperty(author);
        this.press = new SimpleStringProperty(press);
        this.pubYear = new SimpleStringProperty(pubYear);
        this.rentDate = new SimpleStringProperty(rentDate);
        this.dueDate = new SimpleStringProperty(dueDate);
    }

    public BookInfo(String name, String author, String press, String pubYear, String index, int num) {
        this.name = new SimpleStringProperty(name);
        this.author = new SimpleStringProperty(author);
        this.press = new SimpleStringProperty(press);
        this.pubYear = new SimpleStringProperty(pubYear);
        this.index = new SimpleStringProperty(index);
        this.num = new SimpleIntegerProperty(num);
    }

    public CheckBox getChoice() { return choice.get(); }
    public String getName() { return name.get(); }
    public String getAuthor() { return author.get(); }
    public String getPress() { return press.get(); }
    public String getPubYear() { return pubYear.get(); }
    public String getRentDate() { return rentDate.get(); }
    public String getDueDate() { return dueDate.get(); }
    public String getIndex() { return index.get(); }
    public int getNum() { return num.get(); }

    public void setChoice(CheckBox choice) { this.choice.set(choice); }
    public void setName(String name) { this.name.set(name); }
    public void setAuthor(String author) { this.author.set(author); }
    public void setPress(String press) { this.press.set(press); }
    public void setPubYear(String pubYear) { this.pubYear.set(pubYear); }
    public void setRentDate(String rentDate) { this.rentDate.set(rentDate); }
    public void setDueDate(String dueDate) { this.dueDate.set(dueDate); }
    public void setIndex(String index) { this.index.set(index); }
    public void setNum(int num) { this.num.set(num); }

    public ObjectProperty choiceProperty() { return choice; }
    public StringProperty nameProperty() { return name; }
    public StringProperty authorProperty() { return author; }
    public StringProperty pressProperty() { return press; }
    public StringProperty pubYearProperty() { return pubYear; }
    public StringProperty rentDateProperty() { return rentDate; }
    public StringProperty dueDateProperty() { return dueDate; }
    public StringProperty indexProperty() { return index; }
    public IntegerProperty numProperty() { return num; }
}
