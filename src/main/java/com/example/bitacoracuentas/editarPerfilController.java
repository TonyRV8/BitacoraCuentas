package com.example.bitacoracuentas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class editarPerfilController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField apellidosField;

    @FXML
    private TextField correoField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmarPasswordField;

    @FXML
    private Button guardarButton;

    @FXML
    private Button eliminarButton;

    @FXML
    private Label avisoLabel;

    @FXML
    public void initialize() {
        // Cargar datos del usuario actual al cargar el módulo
        cargarDatosUsuario();

        // Configurar el botón de guardar
        guardarButton.setOnAction(event -> guardarCambios());

        // Configurar el botón de eliminar
        eliminarButton.setOnAction(event -> confirmarEliminacion());
    }

    private void cargarDatosUsuario() {
        String query = "SELECT username, nombre, apellidos, correo FROM usuarios WHERE id_usuario = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Session.getUsuarioId()); // Obtener el ID del usuario actual
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    usernameField.setText(resultSet.getString("username"));
                    nombreField.setText(resultSet.getString("nombre"));
                    apellidosField.setText(resultSet.getString("apellidos"));
                    correoField.setText(resultSet.getString("correo"));

                    setAviso("Datos cargados correctamente.", "success");
                } else {
                    setAviso("Error: Usuario no encontrado.", "error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            setAviso("Error al cargar los datos del usuario.", "error");
        }
    }

    private void guardarCambios() {
        String username = usernameField.getText();
        String nombre = nombreField.getText();
        String apellidos = apellidosField.getText();
        String correo = correoField.getText();
        String password = passwordField.getText();
        String confirmarPassword = confirmarPasswordField.getText();

        // Validar campos
        if (username.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty()) {
            setAviso("Por favor, completa todos los campos obligatorios.", "warning");
            return;
        }

        if (!password.isEmpty()) {
            if (!validatePassword(password)) {
                setAviso("La contraseña debe tener más de 6 caracteres, incluir al menos 1 mayúscula, 1 minúscula y 1 número.", "warning");
                return;
            }
            if (!password.equals(confirmarPassword)) {
                setAviso("Las contraseñas no coinciden.", "warning");
                return;
            }
        }

        // Actualizar datos del usuario
        String query = "UPDATE usuarios SET username = ?, nombre = ?, apellidos = ?, correo = ?, password = COALESCE(?, password) WHERE id_usuario = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, nombre);
            statement.setString(3, apellidos);
            statement.setString(4, correo);

            // Encriptar la contraseña si se ingresó
            String hashedPassword = password.isEmpty() ? null : BCrypt.hashpw(password, BCrypt.gensalt());
            statement.setString(5, hashedPassword);

            statement.setInt(6, Session.getUsuarioId());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                setAviso("Datos actualizados correctamente.", "success");
            } else {
                setAviso("Error al actualizar los datos.", "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            setAviso("Error al guardar los cambios.", "error");
        }
    }

    private boolean validatePassword(String password) {
        return password.length() > 6 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*");
    }

    private void confirmarEliminacion() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar tu cuenta?");
        alert.setContentText("Esta acción no se puede deshacer.");

        ButtonType botonConfirmar = new ButtonType("Eliminar");
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(botonConfirmar, botonCancelar);

        alert.showAndWait().ifPresent(response -> {
            if (response == botonConfirmar) {
                eliminarCuenta();
            } else {
                setAviso("Eliminación cancelada.", "info");
            }
        });
    }

    private void eliminarCuenta() {
        String query = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Session.getUsuarioId());
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                setAviso("Cuenta eliminada correctamente. Redirigiendo...", "success");

                // Cerrar sesión y redirigir al login
                Session.cerrarSesion();
                redirigirAlLogin();
            } else {
                setAviso("Error al eliminar la cuenta.", "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            setAviso("Error al eliminar la cuenta.", "error");
        }
    }

    private void redirigirAlLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/login.fxml"));
            Parent loginRoot = loader.load();

            Scene scene = new Scene(loginRoot);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Inicio de Sesión");
        } catch (Exception e) {
            e.printStackTrace();
            setAviso("Error al redirigir al login.", "error");
        }
    }

    private void setAviso(String mensaje, String tipo) {
        avisoLabel.setText(mensaje);
        switch (tipo) {
            case "success" -> avisoLabel.setStyle("-fx-text-fill: green;");
            case "error" -> avisoLabel.setStyle("-fx-text-fill: red;");
            case "warning" -> avisoLabel.setStyle("-fx-text-fill: orange;");
            case "info" -> avisoLabel.setStyle("-fx-text-fill: blue;");
        }
    }
}
