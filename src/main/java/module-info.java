module com.example.bitacoracuentas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.bitacoracuentas to javafx.fxml;
    exports com.example.bitacoracuentas;
}