package com.example.bitacoracuentas;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActualizarGastoController {

    @FXML
    private TextField nombreGastoField;
    @FXML
    private TextField montoTotalField;
    @FXML
    private TextField montoUsadoField;
    @FXML
    private Label mensajeLabel;

    private int idPresupuesto;

    public void cargarDatos(int idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
        String query = "SELECT nombre, monto_total, monto_usado FROM presupuestos WHERE id_presupuestos = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idPresupuesto);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                nombreGastoField.setText(resultSet.getString("nombre"));
                montoTotalField.setText(String.valueOf(resultSet.getDouble("monto_total")));
                montoUsadoField.setText(String.valueOf(resultSet.getDouble("monto_usado")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardarCambios() {
        double montoTotal = Double.parseDouble(montoTotalField.getText());
        double montoUsado = Double.parseDouble(montoUsadoField.getText());

        if (montoUsado > montoTotal) {
            mensajeLabel.setText("No puedes exceder el gasto.");
            return;
        }

        String query = "UPDATE presupuestos SET monto_total = ?, monto_usado = ?, fecha_actualizacion = CURRENT_DATE, " +
                "fecha_fin = CASE WHEN ? >= ? THEN CURRENT_DATE ELSE NULL END WHERE id_presupuestos = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, montoTotal);
            statement.setDouble(2, montoUsado);
            statement.setDouble(3, montoUsado);
            statement.setDouble(4, montoTotal);
            statement.setInt(5, idPresupuesto);

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas > 0) {
                mensajeLabel.setText("Gasto actualizado correctamente.");
            } else {
                mensajeLabel.setText("Error al actualizar el gasto.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mensajeLabel.setText("Error en la base de datos.");
        }
    }

    private int id_Presupuesto;

    public void setIdPresupuesto(int id_Presupuesto_) {
        id_Presupuesto = id_Presupuesto_;
        cargarDatos(id_Presupuesto); // Método para cargar los datos en los campos de texto
    }



}
