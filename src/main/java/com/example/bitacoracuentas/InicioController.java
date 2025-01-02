package com.example.bitacoracuentas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class InicioController {

    @FXML
    private Label saldoQuincenaLabel;

    @FXML
    private ListView<String> graficosListView;

    @FXML
    private ListView<String> educacionListView;

    @FXML
    private Button cerrarSesionButton;

    @FXML
    public void mostrarAdeudos(ActionEvent event) {
        // Lógica para mostrar la pantalla o módulo de Adeudos
        System.out.println("Navegando al módulo de Adeudos...");
    }

    @FXML
    public void mostrarIngresos(ActionEvent event) {
        // Lógica para mostrar la pantalla o módulo de Ingresos
        System.out.println("Navegando al módulo de Ingresos...");
    }

    @FXML
    public void mostrarVistaGeneral(ActionEvent event) {
        // Lógica para mostrar la pantalla o módulo de Ingresos
        System.out.println("Navegando por vista general...");
    }

    @FXML
    public void mostrarDeudas(ActionEvent event) {
        // Lógica para mostrar la pantalla o módulo de Deudas
        System.out.println("Navegando al módulo de Deudas...");
    }

    @FXML
    public void mostrarInversiones(ActionEvent event) {
        // Lógica para mostrar la pantalla o módulo de Inversiones
        System.out.println("Navegando al módulo de Inversiones...");
    }

    @FXML
    public void mostrarPresupuestos(ActionEvent event) {
        // Lógica para mostrar la pantalla o módulo de Presupuestos
        System.out.println("Navegando al módulo de Presupuestos...");
    }

    @FXML
    public void actualizarSaldo(ActionEvent event) {
        // Lógica para actualizar el saldo de la quincena
        double nuevoSaldo = 1000.00; // Ejemplo de valor actualizado (reemplazar con lógica real)
        saldoQuincenaLabel.setText(String.format("%.2f", nuevoSaldo));
        System.out.println("Saldo de la quincena actualizado a: " + nuevoSaldo);
    }

    @FXML
    public void cerrarSesion(ActionEvent event) {
        try {
            // Cambiar de escena al formulario de login
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("login.fxml"));
            javafx.scene.Scene loginScene = new javafx.scene.Scene(loader.load());
            Stage stage = (Stage) cerrarSesionButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Inicio de Sesión");
            System.out.println("Sesión cerrada. Redirigiendo al login.");
        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Inicializar datos de las listas y el saldo al cargar la vista
        saldoQuincenaLabel.setText("0.00"); // Valor inicial del saldo
        cargarGraficos();
        cargarRecursosEducativos();
    }

    private void cargarGraficos() {
        // Lógica para cargar los gráficos financieros en la lista
        graficosListView.getItems().addAll(
                "Gráfico de Ingresos vs. Gastos",
                "Tendencia de Ahorros",
                "Distribución de Presupuesto"
        );
    }

    private void cargarRecursosEducativos() {
        // Lógica para cargar los recursos educativos en la lista
        educacionListView.getItems().addAll(
                "Cómo iniciar un fondo de ahorro",
                "Estrategias básicas de inversión",
                "Consejos para reducir deudas"
        );
    }
}
