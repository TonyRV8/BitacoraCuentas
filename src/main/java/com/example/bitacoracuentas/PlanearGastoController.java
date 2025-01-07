package com.example.bitacoracuentas;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlanearGastoController {

    @FXML
    private TextField nombreGastoField;
    @FXML
    private TextField montoTotalField;
    @FXML
    private Label mensajeLabel;

    @FXML
    private void guardarGasto() {
        String nombreGasto = nombreGastoField.getText();
        double montoTotal;

        try {
            montoTotal = Double.parseDouble(montoTotalField.getText());
        } catch (NumberFormatException e) {
            mensajeLabel.setText("El monto debe ser un número válido.");
            return;
        }

        if (nombreGasto.isEmpty() || montoTotal <= 0) {
            mensajeLabel.setText("Completa todos los campos correctamente.");
            return;
        }

        String query = "INSERT INTO presupuestos (usuario_id, nombre, monto_total, gasto) VALUES (?, ?, ?, TRUE)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Session.getUsuarioId());
            statement.setString(2, nombreGasto);
            statement.setDouble(3, montoTotal);

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                mensajeLabel.setText("Gasto agregado correctamente.");
                PresupuestosController.cargarPresupuestosDesdeBD();
                cerrarVentana();
            } else {
                mensajeLabel.setText("Error al guardar el gasto.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mensajeLabel.setText("Error en la base de datos.");
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nombreGastoField.getScene().getWindow();
        stage.close();
    }
}
