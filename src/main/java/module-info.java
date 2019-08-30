module com.mcdollibee {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mcdollibee to javafx.fxml;
    exports com.mcdollibee;
}