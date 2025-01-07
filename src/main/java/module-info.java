module com.example.bitacoracuentas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires itextpdf;


    opens com.example.bitacoracuentas to javafx.fxml;
    exports com.example.bitacoracuentas;
}