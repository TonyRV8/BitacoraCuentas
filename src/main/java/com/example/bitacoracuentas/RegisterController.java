package com.example.bitacoracuentas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;


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
    private Label registerLabel;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

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
        } else if (!validateEmail(email)) {
            registerLabel.setText("El correo electrónico no es válido.");
        } else if (isUserExists(username)) {
            registerLabel.setText("El nombre de usuario ya existe.");
        } else if (!validatePassword(password)) {
            registerLabel.setText("La contraseña debe tener al menos 7 caracteres, una mayúscula, una minúscula y un número.");
        } else if (!password.equals(confirmPassword)) {
            registerLabel.setText("Las contraseñas no coinciden.");
        } else {
            String hashedPassword = hashPassword(password); // Hash de la contraseña
            if (registerUser(username, nombre, apellido, email, hashedPassword)) {
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

    private boolean validatePassword(String password) {
        return password.length() > 6 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*");
    }

    private boolean validateEmail(String email) {
        return email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean isUserExists(String username) {
        String query = "SELECT COUNT(*) FROM usuarios WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar el usuario: " + e.getMessage());
        }
        return false;
    }

    private boolean registerUser(String username, String nombre, String apellido, String email, String hashedPassword) {
        String query = "INSERT INTO usuarios (username, nombre, apellidos, correo, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3, apellido);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, hashedPassword);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar el usuario: " + e.getMessage());
            return false;
        }
    }

    private String hashPassword(String password) {
        // Genera el hash de la contraseña con jBCrypt
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean checkPassword(String plainPassword, String hashedPassword) {
        // Compara la contraseña ingresada con el hash almacenado
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    private void returnToLogin() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("login.fxml"));
            javafx.scene.Scene loginScene = new javafx.scene.Scene(loader.load());
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Inicio de Sesión");
        } catch (Exception e) {
            System.err.println("Error al cargar el formulario de login: " + e.getMessage());
        }
    }
}
