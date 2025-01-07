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

    private String tipoCategoria; // Tipo de categoría: "Ingresos" o "Adeudos"
    private IngresosController ingresosController; // Referencia al controlador de ingresos
    private AdeudosController adeudosController;   // Referencia al controlador de adeudos

    @FXML
    public void initialize() {
        // Cargar opciones en el ComboBox si no se establece un tipo de categoría
        if (tipoCategoriaComboBox != null && tipoCategoria == null) {
            tipoCategoriaComboBox.getItems().addAll("Ingresos", "Adeudos");
        }

        // Configurar eventos de los botones
        guardarButton.setOnAction(event -> guardarCategoria());
        cancelarButton.setOnAction(event -> cerrarVentana());
    }

    public void setTipoCategoria(String tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }

    public void setIngresosController(IngresosController controller) {
        this.ingresosController = controller;
    }

    public void setAdeudosController(AdeudosController controller) {
        this.adeudosController = controller;
    }

    private void guardarCategoria() {
        String nombre = nombreCategoriaField.getText();
        String tipo = tipoCategoria != null ? tipoCategoria : tipoCategoriaComboBox.getValue();

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

            // Notificar al controlador correspondiente
            if (ingresosController != null) {
                ingresosController.cargarCategorias();
            } else if (adeudosController != null) {
                adeudosController.cargarCategorias();
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
