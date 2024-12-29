package com.example.bitacoracuentas;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import javafx.event.ActionEvent;



public class LoginController {
    @FXML
    private Label loginLabel;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;

    public void loginButtonOnAction (ActionEvent e) {

        if (userField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            loginLabel.setText("Completa los campos");
        }else{
            loginLabel.setText("Loguianding");
        }
    }


}

