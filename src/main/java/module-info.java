module com.example.bitacoracuentas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires jbcrypt;


    opens com.example.bitacoracuentas to javafx.fxml;
    exports com.example.bitacoracuentas;
}