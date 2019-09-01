module com.mcdollibee {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires sqlite.jdbc;

    opens com.mcdollibee to javafx.fxml;
    exports com.mcdollibee;
}