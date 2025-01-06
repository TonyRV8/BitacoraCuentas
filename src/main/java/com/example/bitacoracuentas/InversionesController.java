package com.example.bitacoracuentas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.bitacoracuentas.Session.getUsuarioId;

public class InversionesController {

    public Button insertarButton;
    @FXML
    private TableView<Inversion> inversionesTable;
    @FXML
    private TableColumn<Inversion, String> colDescripcion;
    @FXML
    private TableColumn<Inversion, Double> colMonto;
    @FXML
    private TableColumn<Inversion, Double> colRendimiento;
    @FXML
    private TableColumn<Inversion, Date> colFechaInicio;
    @FXML
    private TableColumn<Inversion, Date> colFechaFin;
    @FXML

    private ObservableList<Inversion> inversionesData = FXCollections.observableArrayList();

    public void initialize() {
        // Configuración de las columnas
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colRendimiento.setCellValueFactory(new PropertyValueFactory<>("rendimientoEstimado"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));

        int id_usuario= getUsuarioId();
        cargarInversiones(id_usuario); // Aquí se debe usar el idUsuario autenticado
    }

    // Método para cargar las inversiones desde la base de datos
    private void cargarInversiones(int usuarioId) {
        String sql = "SELECT descripcion, monto, rendimiento_estimado, fecha_inicio, fecha_fin FROM inversiones WHERE usuario_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            List<Inversion> listaInversiones = new ArrayList<>();

            while (rs.next()) {
                String descripcion = rs.getString("descripcion");
                double monto = rs.getDouble("monto");
                Double rendimientoEstimado = rs.getDouble("rendimiento_estimado");
                Date fechaInicio = rs.getDate("fecha_inicio");
                Date fechaFin = rs.getDate("fecha_fin");

                listaInversiones.add(new Inversion(descripcion, monto, rendimientoEstimado, fechaInicio, fechaFin));
            }

            // Asignar la lista de inversiones a la tabla
            inversionesData.setAll(listaInversiones);
            inversionesTable.setItems(inversionesData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Método para mostrar alertas de error
    private void showErrorAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para abrir el formulario de inversión
    @FXML
    public void abrirFormularioInsercion(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FormularioInversion.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Agregar Inversión");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Esto imprimirá detalles más específicos en la consola
            showErrorAlert("Error al abrir el formulario de inversión: " + e.getMessage());
        }
    }


}
