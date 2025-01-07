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

import javax.swing.text.html.ImageView;
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
    private Button educacionButton;

    @FXML
    private Button analisisButton;

    @FXML
    private Button exportacionButton;

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

        // Configuración inicial de eventos para cada botón
        quincenaButton.setOnAction(event -> {
            cargarModuloQuincena();
            setActiveButton(quincenaButton);
        });
        ingresosButton.setOnAction(event -> {
            cargarModuloIngresos();
            setActiveButton(ingresosButton);
        });
        adeudosButton.setOnAction(event -> {
            cargarModuloAdeudos();
            setActiveButton(adeudosButton);
        });
        deudasButton.setOnAction(event -> {
            cargarModuloDeudas();
            setActiveButton(deudasButton);
        });
        inversionesButton.setOnAction(event -> {
            cargarModuloInversiones();
            setActiveButton(inversionesButton);
        });
        presupuestosButton.setOnAction(event -> {
            cargarModuloPresupuestos();
            setActiveButton(presupuestosButton);
        });
        educacionButton.setOnAction(event -> {
            cargarModuloConsejos();
            setActiveButton(educacionButton);
        });
        editarButton.setOnAction(event -> {
            cargarModuloEditarPerfil();
            setActiveButton(editarButton);
        });
        analisisButton.setOnAction(event -> {
            cargarModuloAnalisis();
            setActiveButton(analisisButton);
        });
        exportacionButton.setOnAction(event -> {
            cargarModuloExportar();
            setActiveButton(exportacionButton);
        });

        // Configurar el evento del botón logout
        logoutButton.setOnAction(event -> logout());


    }

    private void setActiveButton(Button button) {
        // Quitar la clase "active" del botón actualmente activo
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }

        // Establecer el nuevo botón como activo
        button.getStyleClass().add("active");
        activeButton = button;
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

    private void cargarModuloConsejos() {
        try {
            // Cargar el archivo FXML del módulo de consejos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/consejos.fxml"));
            Node consejosView = loader.load();

            // Obtener el controlador de Consejos
            ConsejosController consejosController = loader.getController();

            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(consejosView);
            System.out.println("Cargando módulo de consejos.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de consejos.");
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

    private void cargarModuloAnalisis() {
        try {
            // Cargar el archivo FXML del módulo de análisis
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/analisis.fxml"));
            Node analisisView = loader.load();

            // Obtener el controlador del módulo
            AnalisisController analisisController = loader.getController();

            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(analisisView);

            // Imprimir mensaje en consola
            System.out.println("Cargando módulo de análisis de tendencias financieras");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de análisis de tendencias financieras.");
        }
    }

    private void cargarModuloExportar() {
        try {
            // Cargar el archivo FXML del módulo de exportación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/exportar.fxml"));
            Node exportarView = loader.load();

            // Obtener el controlador del módulo
            ExportarController exportarController = loader.getController();

            // Limpiar el StackPane y cargar el nuevo módulo
            contenidoStackPane.getChildren().clear();
            contenidoStackPane.getChildren().add(exportarView);

            // Imprimir mensaje en consola
            System.out.println("Cargando módulo de exportación de datos");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar el módulo de exportación de datos.");
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
