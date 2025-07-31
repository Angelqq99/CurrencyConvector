module com.example.currencyconvector {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;
    requires java.desktop;


    opens com.example.currencyconvector to javafx.fxml;
    exports com.example.currencyconvector;
}