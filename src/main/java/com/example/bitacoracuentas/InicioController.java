package com.example.bitacoracuentas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class InicioController {

    @FXML
    private Pane topPane; // Pane superior para mover la ventana


    @FXML
    private AnchorPane root; // Asegúrate de que el fx:id en el FXML coincida con este nombre

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private VBox sidebar; // Contenedor del menú lateral

    private Button activeButton = null; // Referencia al botón activo

    @FXML
    private Button logoutButton; // Botón para cerrar sesión

    @FXML
    private StackPane contenidoStackPane; // StackPane en el centro

    @FXML
    private Button quincenaButton; // Botón Saldo de Quincena

    @FXML
    private Button ingresosButton;

    @FXML
    private Button adeudosButton;

    @FXML
    private Button deudasButton;

    @FXML
    private Button inversionesButton;
    @FXML
    private Button presupuestosButton;


    @FXML
    private Button editarButton;


    @FXML
    public void initialize() {
        // Movimiento de la ventana con el Pane superior
        topPane.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) topPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Encuentra todos los botones dentro del VBox
        List<Button> buttons = sidebar.getChildren().filtered(node -> node instanceof Button)
                .stream()
                .map(node -> (Button) node) // Convierte explícitamente cada Node a Button
                .toList(); // Convierte el resultado a un List<Button>

        // Agrega el comportamiento de "estado activo" a cada botón
        for (Button button : buttons) {
            button.setOnAction(event -> setActiveButton(button));
        }
    }

    private void setActiveButton(Button button) {
        // Quita la clase "active" del botón actualmente activo
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }

        // Establece el nuevo botón como activo
        button.getStyleClass().add("active");
        activeButton = button;

        quincenaButton.setOnAction(event -> cargarModuloQuincena());
        ingresosButton.setOnAction(event -> cargarModuloIngresos());
        adeudosButton.setOnAction(event -> cargarModuloAdeudos());
        deudasButton.setOnAction(event -> cargarModuloDeudas());
        inversionesButton.setOnAction(event -> cargarModuloInversiones());
        presupuestosButton.setOnAction(event -> cargarModuloPresupuestos());
        editarButton.setOnAction(event -> cargarModuloEditarPerfil());



        // Configurar el evento del botón logout
        logoutButton.setOnAction(event -> logout());
    }

    private void cargarModuloQuincena() {
        try {
            // Cargar el archivo FXML del módulo de quincena
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/quincena.fxml"));
            Node quincenaView = loader.load();

            // Obtener el controlador de Quincena
            QuincenaController quincenaController = loader.getController();


            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(quincenaView);
            System.out.println("Cargando módulo quincena");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de Saldo de Quincena.");
        }
    }

    private void cargarModuloIngresos() {
        try {
            // Cargar el archivo FXML del módulo de ingresos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/ingresos.fxml"));
            Node ingresosView = loader.load();

            // Obtener el controlador de Ingresos
            IngresosController ingresosController = loader.getController();

            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(ingresosView);
            System.out.println("Cargando módulo ingresos");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de ingresos.");
        }
    }

    private void cargarModuloAdeudos() {
        try {
            // Cargar el archivo FXML del módulo de adeudos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/adeudos.fxml"));
            Node adeudosView = loader.load();

            // Obtener el controlador de Adeudos
            AdeudosController adeudosController = loader.getController();

            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(adeudosView);
            System.out.println("Cargando módulo adeudos");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de adeudos.");
        }
    }

    private void cargarModuloDeudas() {
        try {
            // Cargar el archivo FXML del módulo de deudas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/deudas.fxml"));
            Node deudasView = loader.load();

            // Obtener el controlador de Deudas
            DeudasController deudasController = loader.getController();

            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(deudasView);
            System.out.println("Cargando módulo deudas");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de deudas.");
        }
    }

    private void cargarModuloPresupuestos() {
        try {
            // Cargar el archivo FXML del módulo de inversiones
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/presupuestos.fxml"));
            Node presupuestosView = loader.load();
            PresupuestosController presupuestosController = loader.getController();
            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(presupuestosView);
            System.out.println("Cargando módulo presupuestos");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de presupuestos.");
        }
    }

    private void cargarModuloInversiones() {
        try {
            // Cargar el archivo FXML del módulo de inversiones
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/inversiones.fxml"));
            Node inversionesView = loader.load();

            InversionesController inversionesController = loader.getController();

            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(inversionesView);
            System.out.println("Cargando módulo Inversiones");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de Inversiones.");
        }
    }

    private void cargarModuloEditarPerfil() {
        try {
            // Cargar el archivo FXML del módulo de Editar Perfil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/editarPerfil.fxml"));
            Node editarPerfilView = loader.load();

            // Obtener el controlador de Editar Perfil (si necesitas pasar datos, por ejemplo)
            editarPerfilController editarPerfilController = loader.getController();

            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(editarPerfilView);
            System.out.println("Cargando módulo editar perfil.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de editar perfil.");
        }
    }



    private void logout() {
        Session.cerrarSesion(); // Limpiar los datos de la sesión
        System.out.println("Sesión cerrada.");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Inicio de Sesión");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la pantalla de inicio de sesión.");
        }
    }

}
