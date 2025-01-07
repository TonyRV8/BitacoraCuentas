package com.example.bitacoracuentas;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.bitacoracuentas.Session.getUsuarioId;

public class FormularioInversionController {

    @FXML
    private TextField descripcionField;

    @FXML
    private TextField montoField;

    @FXML
    private TextField rendimientoField;

    @FXML
    private DatePicker fechaInicioField;

    @FXML
    private DatePicker fechaFinField;

    @FXML
    private Button guardarButton;

    @FXML
    private Button cancelarButton;

    // Método para guardar la inversión
    @FXML
    private void guardarInversion() {
        // Recuperar los valores de los campos
        String descripcion = descripcionField.getText();
        String montoStr = montoField.getText();
        String rendimientoStr = rendimientoField.getText();
        String fechaInicio = fechaInicioField.getValue() != null ? fechaInicioField.getValue().toString() : null;
        String fechaFin = fechaFinField.getValue() != null ? fechaFinField.getValue().toString() : null;

        // Validaciones
        if (descripcion.isEmpty() || montoStr.isEmpty() || rendimientoStr.isEmpty() || fechaInicio == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios, excepto la fecha de fin.", AlertType.ERROR);
            return;
        }

        // Validar que el monto sea un número
        double monto = 0;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El monto ingresado no es válido.", AlertType.ERROR);
            return;
        }

        // Validar que el rendimiento estimado sea un número
        double rendimiento = 0;
        try {
            rendimiento = Double.parseDouble(rendimientoStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El rendimiento estimado no es válido.", AlertType.ERROR);
            return;
        }

        // Guardar la inversión en la base de datos
        insertarInversion(descripcion, monto, rendimiento, fechaInicio, fechaFin);
    }

    // Método para insertar la inversión en la base de datos
    private void insertarInversion(String descripcion, double monto, double rendimiento, String fechaInicio, String fechaFin) {
        String sql = "INSERT INTO inversiones (usuario_id, descripcion, monto, rendimiento_estimado, fecha_inicio, fecha_fin) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int id_usuario= getUsuarioId();
            // Suponiendo que el ID del usuario está almacenado en una variable estática llamada "idUsuario"
            pstmt.setInt(1, id_usuario); // Aquí recuperamos el id del usuario
            pstmt.setString(2, descripcion);
            pstmt.setDouble(3, monto);
            pstmt.setDouble(4, rendimiento);
            pstmt.setDate(5, java.sql.Date.valueOf(fechaInicio));
            pstmt.setDate(6, fechaFin != null ? java.sql.Date.valueOf(fechaFin) : null);

            pstmt.executeUpdate();
            mostrarAlerta("Éxito", "La inversión ha sido guardada correctamente.", AlertType.INFORMATION);
            limpiarFormulario();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Hubo un error al guardar la inversión. Inténtalo nuevamente.", AlertType.ERROR);
        }
    }

    // Método para limpiar el formulario
    private void limpiarFormulario() {
        descripcionField.clear();
        montoField.clear();
        rendimientoField.clear();
        fechaInicioField.setValue(null);
        fechaFinField.setValue(null);
    }

    // Método para mostrar una alerta
    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Método para cancelar el formulario
    @FXML
    private void cancelarFormulario() {
        limpiarFormulario();
    }
}

