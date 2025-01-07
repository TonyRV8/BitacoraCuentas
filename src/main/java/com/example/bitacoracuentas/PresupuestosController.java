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

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PresupuestosController {

    @FXML
    private TextArea consejoFinancieroField;

    @FXML
    private ListView<HBox> gastosPlaneadosList;

    @FXML
    private ListView<String> ingresosPrevistosList;

    private static final ObservableList<HBox> gastosPlaneados = FXCollections.observableArrayList();
    private static final ObservableList<String> ingresosPrevistos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        consejoFinancieroField.setText(generarConsejoFinanciero());
        gastosPlaneadosList.setItems(gastosPlaneados);
        ingresosPrevistosList.setItems(ingresosPrevistos);

        cargarPresupuestosDesdeBD();
    }
    public String generarConsejoFinanciero() {
        int usuarioId = Session.getUsuarioId();
        StringBuilder consejo = new StringBuilder();

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Consultar adeudos
            String queryAdeudos = "SELECT COUNT(*) FROM adeudos WHERE usuario_id = ? AND saldo_restante > 0";
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryAdeudos)) {
                preparedStatement.setInt(1, usuarioId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    consejo.append("Recuerda pagar tus deudas para no generar intereses.\n");
                }
            }

            // Consultar inversiones
            String queryInversiones = "SELECT COUNT(*) FROM inversiones WHERE usuario_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryInversiones)) {
                preparedStatement.setInt(1, usuarioId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    consejo.append("Revisa tus inversiones para asegurarte de que están rindiendo como esperas.\n");
                }
            }

            // Consultar presupuestos
            String queryPresupuestos = "SELECT COUNT(*) FROM presupuestos WHERE usuario_id = ? AND gasto = TRUE";
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryPresupuestos)) {
                preparedStatement.setInt(1, usuarioId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    consejo.append("Revisa tus presupuestos para mantener tus gastos bajo control.\n");
                }
            }

            // Consultar saldo quincenal y comparar con gastos de la última quincena
            String querySaldoQuincena = "SELECT saldo FROM saldo_quincena WHERE usuario_id = ? ORDER BY id_saldo DESC LIMIT 1";
            double saldoQuincena = 0;
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySaldoQuincena)) {
                preparedStatement.setInt(1, usuarioId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    saldoQuincena = resultSet.getDouble("saldo");
                }
            }

            String queryGastosQuincena = "SELECT nombre, monto_total FROM presupuestos WHERE usuario_id = ? AND gasto = TRUE AND fecha_fin >= CURRENT_DATE - INTERVAL '15 days'";
            double totalGastosQuincena = 0;
            List<String> presupuestosExcedidos = new ArrayList<>();
            try (PreparedStatement preparedStatement = connection.prepareStatement(queryGastosQuincena)) {
                preparedStatement.setInt(1, usuarioId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    double montoTotal = resultSet.getDouble("monto_total");
                    totalGastosQuincena += montoTotal;
                    if (totalGastosQuincena > saldoQuincena * 0.8) {
                        presupuestosExcedidos.add(resultSet.getString("nombre"));
                    }
                }
            }

            if (!presupuestosExcedidos.isEmpty()) {
                consejo.append("Recomendamos bajar los gastos en la quincena. Los siguientes presupuestos exceden el 20% del saldo quincenal:\n");
                for (String presupuesto : presupuestosExcedidos) {
                    consejo.append("- ").append(presupuesto).append("\n");
                }
            }

            // Mensaje por defecto si no se cumplen las condiciones anteriores
            if (consejo.length() == 0) {
                consejo.append("Visita la sección de educación financiera para aprender a maximizar tu dinero.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            consejo.append("Error al generar el consejo financiero.");
        }

        return consejo.toString();
    }

    public static void cargarPresupuestosDesdeBD() {
        // Limpiar las listas antes de agregar nuevos elementos
        gastosPlaneados.clear();
        ingresosPrevistos.clear();

        String query = "SELECT id_presupuestos, nombre, monto_total, monto_usado, gasto, fecha_fin FROM presupuestos WHERE usuario_id = ?";
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
                Date fechaFin = resultSet.getDate("fecha_fin");

                if (gasto) {
                    Label label = new Label(nombre + " - Usado: " + montoUsado + " / " + montoTotal);
                    Button actualizarButton = new Button("Actualizar");
                    actualizarButton.setOnAction(event -> abrirActualizarGasto(idPresupuesto));
                    actualizarButton.setStyle("-fx-background-color: #33A1FD; -fx-text-fill: #FFFFFF;");

                    HBox item = montoUsado < montoTotal ? new HBox(10, label, actualizarButton) : new HBox(label, new Label("Completado"));
                    gastosPlaneados.add(item);
                } else {
                    ingresosPrevistos.add(nombre + " - Ingreso: " + montoTotal + " - Fecha de Pago: " + fechaFin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void abrirActualizarGasto(int idPresupuesto) {
        try {
            FXMLLoader loader = new FXMLLoader(PresupuestosController.class.getResource("actualizarGasto.fxml"));
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PlanearGasto.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Planear Nuevo Gasto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void preverIngreso() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PreverIngreso.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Prever Nuevo Ingreso");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}