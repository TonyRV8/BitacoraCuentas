package com.example.bitacoracuentas;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class PreverIngresoController {

    @FXML
    private TextField nombreIngresoField;
    @FXML
    private TextField montoTotalField;
    @FXML
    private DatePicker fechaFinPicker;
    @FXML
    private Label mensajeLabel;

    @FXML
    private void guardarIngreso() {
        String nombreIngreso = nombreIngresoField.getText();
        double montoTotal;

        try {
            montoTotal = Double.parseDouble(montoTotalField.getText());
        } catch (NumberFormatException e) {
            mensajeLabel.setText("El monto debe ser un número válido.");
            return;
        }

        LocalDate fechaFin = fechaFinPicker.getValue();
        if (fechaFin == null) {
            mensajeLabel.setText("Debes seleccionar una fecha estimada.");
            return;
        }

        if (fechaFin.isBefore(LocalDate.now())) {
            mensajeLabel.setText("No se pueden colocar fechas pasadas.");
            return;
        }

        if (nombreIngreso.isEmpty() || montoTotal <= 0) {
            mensajeLabel.setText("Todos los campos deben ser completados correctamente.");
            return;
        }

        // Inserción en la base de datos
        String query = "INSERT INTO presupuestos (usuario_id, nombre, monto_total, gasto, fecha_fin) VALUES (?, ?, ?, FALSE, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Session.getUsuarioId());
            statement.setString(2, nombreIngreso);
            statement.setDouble(3, montoTotal);
            statement.setDate(4, java.sql.Date.valueOf(fechaFin));

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                mensajeLabel.setText("Ingreso agregado correctamente.");
                cerrarVentana();
                PresupuestosController.cargarPresupuestosDesdeBD();
            } else {
                mensajeLabel.setText("Error al guardar el ingreso.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mensajeLabel.setText("Error en la base de datos.");
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nombreIngresoField.getScene().getWindow();
        stage.close();
    }
}
