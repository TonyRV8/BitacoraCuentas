package com.example.bitacoracuentas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField apellidoField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label registerLabel;

    @FXML
    public void registerButtonOnAction(ActionEvent event) {
        String username = usernameField.getText();
        String nombre = nameField.getText();
        String apellido = apellidoField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            registerLabel.setText("Todos los campos son obligatorios.");
        } else if (!password.equals(confirmPassword)) {
            registerLabel.setText("Las contraseñas no coinciden.");
        } else {
            if (registerUser(username, nombre, apellido, email, password)) {
                registerLabel.setText("Registro exitoso.");
                returnToLogin();
            } else {
                registerLabel.setText("Error al registrar el usuario.");
            }
        }
    }

    @FXML
    public void cancelButtonOnAction(ActionEvent event) {
        returnToLogin();
    }

    private boolean registerUser(String username, String nombre, String apellido, String email, String password) {
        String query = "INSERT INTO usuarios (username, nombre, apellidos, correo, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3, apellido);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, password);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar el usuario: " + e.getMessage());
            return false;
        }
    }

    private void returnToLogin() {
        try {
            // Carga el archivo FXML del login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());

            // Obtén el stage actual y cambia la escena
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Inicio de Sesión");
        } catch (IOException e) {
            System.out.println("Error al cargar el formulario de login: " + e.getMessage());
        }
    }
}
