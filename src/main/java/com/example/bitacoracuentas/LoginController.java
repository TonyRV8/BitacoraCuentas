package com.example.bitacoracuentas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.mindrot.jbcrypt.BCrypt;

public class LoginController {

    @FXML
    private Label loginLabel;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private AnchorPane leftPane; // Panel para mover la ventana

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // Configurar movimiento de la ventana con el leftPane
        leftPane.setOnMousePressed(this::handleMousePressed);
        leftPane.setOnMouseDragged(this::handleMouseDragged);
    }

    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) leftPane.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

    public void loginButtonOnAction(ActionEvent e) {
        String username = userField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginLabel.setText("Completa los campos");
        } else {
            // Valida las credenciales con la base de datos
            if (validarCredenciales(username, password)) {
                loginLabel.setText("Logueado con éxito");
                abrirVistaInicio(e);
            } else {
                loginLabel.setText("Usuario o contraseña incorrectos.");
            }
        }
    }

    private boolean validarCredenciales(String username, String password) {
        String query = "SELECT id_usuario, nombre, correo, password FROM usuarios WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Configura el parámetro del nombre de usuario en la consulta
            preparedStatement.setString(1, username);

            // Ejecuta la consulta
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Obtén los datos del usuario desde la base de datos
                int usuarioId = resultSet.getInt("id_usuario");
                String nombre = resultSet.getString("nombre");
                String correo = resultSet.getString("correo");
                String hashedPassword = resultSet.getString("password");

                // Verifica si la contraseña ingresada coincide con la encriptada
                if (BCrypt.checkpw(password, hashedPassword)) {
                    // Establece la sesión del usuario actual
                    Session.iniciarSesion(usuarioId, nombre, correo);
                    System.out.println("Sesión iniciada para el usuario ID: " + usuarioId);
                    return true;
                }
            }

            // Si no se encuentra el usuario o las credenciales no coinciden
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            loginLabel.setText("Error al conectar con la base de datos.");
            return false;
        }
    }


    private void abrirVistaInicio(ActionEvent e) {
        try {
            // Cargar el módulo de inicio
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/inicio.fxml"));
            Parent root = loader.load();

            // Crear un nuevo Stage sin decoración
            Stage inicioStage = new Stage();
            inicioStage.initStyle(StageStyle.UNDECORATED);

            // Configurar la escena del módulo de inicio
            Scene inicioScene = new Scene(root);
            inicioStage.setScene(inicioScene);

            // Mostrar el nuevo Stage
            inicioStage.show();

            // Cerrar el Stage actual (Login)
            Stage loginStage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
            loginStage.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            loginLabel.setText("Error al cargar la pantalla de inicio.");
        }
    }

    public void registerButtonOnAction(ActionEvent e) {
        try {
            // Carga el archivo FXML del formulario de registro
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/register.fxml"));
            Scene registerScene = new Scene(fxmlLoader.load());

            // Obtén el stage actual y cambia la escena
            Stage stage = (Stage) loginLabel.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setTitle("Registro de Cuenta");
        } catch (IOException ex) {
            ex.printStackTrace();
            loginLabel.setText("Error al cargar la pantalla de registro.");
        }
    }
}
