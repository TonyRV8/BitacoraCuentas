package com.example.bitacoracuentas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PresupuestosController {

    @FXML
    private TextField consejoFinancieroField;

    @FXML
    private ListView<HBox> gastosPlaneadosList;

    @FXML
    private ListView<String> ingresosPrevistosList;

    private final ObservableList<HBox> gastosPlaneados = FXCollections.observableArrayList();
    private final ObservableList<String> ingresosPrevistos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        consejoFinancieroField.setText("Ahorra al menos el 20% de tus ingresos.");

        gastosPlaneadosList.setItems(gastosPlaneados);
        ingresosPrevistosList.setItems(ingresosPrevistos);

        cargarPresupuestosDesdeBD();
    }

    private void cargarPresupuestosDesdeBD() {
        String query = "SELECT id_presupuestos, nombre, monto_total, monto_usado, gasto FROM presupuestos WHERE usuario_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, Session.getUsuarioId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idPresupuesto = resultSet.getInt("id_presupuestos");
                String nombre = resultSet.getString("nombre");
                double montoTotal = resultSet.getDouble("monto_total");
                double montoUsado = resultSet.getDouble("monto_usado");
                boolean gasto = resultSet.getBoolean("gasto");

                if (gasto) {
                    Label label = new Label(nombre + " - Usado: " + montoUsado + " / " + montoTotal);
                    Button actualizarButton = new Button("Actualizar");
                    actualizarButton.setOnAction(event -> abrirActualizarGasto(idPresupuesto));

                    HBox item = montoUsado < montoTotal ? new HBox(10, label, actualizarButton) : new HBox(label, new Label("Completado"));
                    gastosPlaneados.add(item);
                } else {
                    ingresosPrevistos.add(nombre + " - Ingreso: " + montoTotal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirActualizarGasto(int idPresupuesto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("actualizarGasto.fxml"));
            Parent root = loader.load();
            ActualizarGastoController controller = loader.getController();
            controller.setIdPresupuesto(idPresupuesto);

            Stage stage = new Stage();
            stage.setTitle("Actualizar Gasto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void planearGasto() {
        System.out.println("Planear gasto seleccionado");
    }

    @FXML
    private void preverIngreso() {
        System.out.println("Prever ingreso seleccionado");
    }
}