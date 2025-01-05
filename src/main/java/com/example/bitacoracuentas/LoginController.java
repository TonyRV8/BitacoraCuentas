package com.example.bitacoracuentas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginController {

    @FXML
    private Label loginLabel;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;

    public void loginButtonOnAction(ActionEvent e) {
        String username = userField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginLabel.setText("Completa los campos");
        } else {
            // Aquí puedes agregar tu lógica para validar el usuario
            loginLabel.setText("Logueado con éxito (placeholder)");

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
                exception.printStackTrace(); // Imprime la traza del error para identificar el problema exacto
            }



        }
    }

    public void registerButtonOnAction(ActionEvent e) {
        try {
            // Carga el archivo FXML del formulario de registro
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
            Scene registerScene = new Scene(fxmlLoader.load());

            // Obtén el stage actual y cambia la escena
            Stage stage = (Stage) loginLabel.getScene().getWindow();
            stage.setScene(registerScene);
            stage.setTitle("Registro de Cuenta");
        } catch (IOException ex) {
            System.out.println("Error al cargar el formulario de registro: " + ex.getMessage());
        }
    }
}
