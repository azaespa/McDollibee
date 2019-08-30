module com.mcdollibee {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mcdollibee to javafx.fxml;
    exports com.mcdollibee;
}