package com.example.bitacoracuentas;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CategoriaController {

    @FXML
    private TextField nombreCategoriaField;

    @FXML
    private ComboBox<String> tipoCategoriaComboBox;

    @FXML
    private Button guardarButton;

    @FXML
    private Button cancelarButton;

    private IngresosController ingresosController; // Referencia al controlador principal

    public void setIngresosController(IngresosController controller) {
        this.ingresosController = controller;
    }

    @FXML
    public void initialize() {
        // Cargar opciones en el ComboBox
        tipoCategoriaComboBox.getItems().addAll("Ingresos", "Adeudos");

        // Configurar eventos de los botones
        guardarButton.setOnAction(event -> guardarCategoria());
        cancelarButton.setOnAction(event -> cerrarVentana());
    }

    private void guardarCategoria() {
        String nombre = nombreCategoriaField.getText();
        String tipo = tipoCategoriaComboBox.getValue();

        if (nombre == null || nombre.isEmpty() || tipo == null) {
            System.out.println("Por favor, completa todos los campos.");
            return;
        }

        String query = "INSERT INTO categorias (usuario_id, nombre, tipo) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Session.getUsuarioId());
            statement.setString(2, nombre);
            statement.setString(3, tipo);

            statement.executeUpdate();
            System.out.println("Nueva categoría guardada: " + nombre);

            // Notificar al controlador principal para recargar categorías
            if (ingresosController != null) {
                ingresosController.cargarCategorias();
            }

            cerrarVentana();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al guardar la nueva categoría.");
        }
    }

    private void cerrarVentana() {
        // Cierra la ventana actual
        Stage stage = (Stage) cancelarButton.getScene().getWindow();
        stage.close();
    }
}
