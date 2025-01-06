package com.example.bitacoracuentas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class IngresosController {

    @FXML
    private TextField descripcionField;

    @FXML
    private ComboBox<String> categoriaComboBox;

    @FXML
    private TextField montoField;

    @FXML
    private TableView<Ingreso> ingresosTable;

    @FXML
    private TableColumn<Ingreso, String> colDescripcion;

    @FXML
    private TableColumn<Ingreso, String> colCategoria;

    @FXML
    private TableColumn<Ingreso, Double> colMonto;

    @FXML
    private TableColumn<Ingreso, LocalDate> colFecha;

    @FXML
    private TableColumn<Ingreso, Void> colEditar;

    @FXML
    private TableColumn<Ingreso, Void> colEliminar;


    @FXML
    private Button guardarIngresoButton;

    @FXML
    private Button nuevaCategoriaButton;

    @FXML
    private Button cancelarIngresoButton;


    private ObservableList<Ingreso> ingresosList;
    private Ingreso ingresoEnEdicion = null; // Variable para rastrear el ingreso en edición

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        // Inicializar lista de ingresos
        ingresosList = FXCollections.observableArrayList();
        ingresosTable.setItems(ingresosList);

        // Cargar categorías desde la base de datos
        cargarCategorias();
        cargarIngresos();
        agregarBotones();


        cancelarIngresoButton.setOnAction(event -> cancelarIngreso());
        guardarIngresoButton.setOnAction(event -> guardarIngreso());
        nuevaCategoriaButton.setOnAction(event -> abrirVentanaNuevaCategoria());

    }

    private void cargarIngresos() {
        String query = "SELECT i.descripcion, c.nombre AS categoria, i.monto, i.fecha_ingreso " +
                "FROM ingresos i " +
                "JOIN categorias c ON i.categoria_id = c.id_categorias " +
                "WHERE i.usuario_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Session.getUsuarioId()); // Obtener el ID del usuario actual

            try (ResultSet resultSet = statement.executeQuery()) {
                ingresosList.clear(); // Limpiar la lista antes de cargar datos

                while (resultSet.next()) {
                    String descripcion = resultSet.getString("descripcion");
                    String categoria = resultSet.getString("categoria");
                    double monto = resultSet.getDouble("monto");
                    LocalDate fecha = resultSet.getDate("fecha_ingreso").toLocalDate();

                    // Crear un nuevo ingreso y agregarlo a la lista
                    Ingreso ingreso = new Ingreso(descripcion, categoria, monto, fecha);
                    ingresosList.add(ingreso);
                }

                System.out.println("Ingresos cargados correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar los ingresos.");
        }
    }


    protected void cargarCategorias() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Usar un conjunto para evitar duplicados
            Set<String> categoriasUnicas = new HashSet<>();

            // Categorías generales
            String queryGenerales = "SELECT nombre FROM categorias_generales WHERE tipo = 'Ingresos'";
            try (PreparedStatement statement = connection.prepareStatement(queryGenerales);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    categoriasUnicas.add(resultSet.getString("nombre"));
                }
            }

            // Categorías personalizadas
            String queryPersonalizadas = "SELECT nombre FROM categorias WHERE usuario_id = ? AND tipo = 'Ingresos'";
            try (PreparedStatement statement = connection.prepareStatement(queryPersonalizadas)) {
                statement.setInt(1, Session.getUsuarioId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        categoriasUnicas.add(resultSet.getString("nombre"));
                    }
                }
            }

            // Actualizar el ComboBox con las categorías únicas
            categoriaComboBox.getItems().clear();
            categoriaComboBox.getItems().addAll(categoriasUnicas);

            System.out.println("Categorías cargadas correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar las categorías.");
        }
    }


    @FXML
    private void guardarIngreso() {
        try {
            String descripcion = descripcionField.getText();
            String categoria = categoriaComboBox.getValue();
            double monto = Double.parseDouble(montoField.getText());

            if (descripcion.isEmpty() || categoria == null || monto <= 0) {
                System.out.println("Por favor, completa todos los campos correctamente.");
                return;
            }

            // Si estamos editando un ingreso, actualízalo
            if (ingresoEnEdicion != null) {
                actualizarIngresoEnBaseDeDatos(descripcion, categoria, monto);
            } else {
                // Si no estamos editando, guarda un nuevo ingreso
                Ingreso nuevoIngreso = new Ingreso(descripcion, categoria, monto, LocalDate.now());
                ingresosList.add(nuevoIngreso);
                guardarIngresoEnBaseDeDatos(nuevoIngreso);
            }

            // Limpiar campos y resetear el ingreso en edición
            descripcionField.clear();
            montoField.clear();
            categoriaComboBox.setValue(null);
            ingresoEnEdicion = null;

            System.out.println("Ingreso guardado correctamente.");
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingresa un monto válido.");
        }
    }

    private void actualizarIngresoEnBaseDeDatos(String nuevaDescripcion, String nuevaCategoria, double nuevoMonto) {
        String query = "UPDATE ingresos SET descripcion = ?, categoria_id = ?, monto = ? " +
                "WHERE descripcion = ? AND usuario_id = ? AND monto = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Obtener el ID de la nueva categoría
            int nuevaCategoriaId = obtenerCategoriaId(nuevaCategoria);
            if (nuevaCategoriaId == -1) {
                System.out.println("Error: Categoría no encontrada. Asegúrate de que la categoría exista en la base de datos.");
                return;
            }

            // Configurar los nuevos valores
            statement.setString(1, nuevaDescripcion);
            statement.setInt(2, nuevaCategoriaId);
            statement.setDouble(3, nuevoMonto);

            // Configurar los valores originales para encontrar el registro
            statement.setString(4, ingresoEnEdicion.getDescripcion());
            statement.setInt(5, Session.getUsuarioId());
            statement.setDouble(6, ingresoEnEdicion.getMonto());

            // Ejecutar la actualización
            statement.executeUpdate();

            // Actualizar el ingreso en la lista
            ingresoEnEdicion.setDescripcion(nuevaDescripcion);
            ingresoEnEdicion.setCategoria(nuevaCategoria);
            ingresoEnEdicion.setMonto(nuevoMonto);
            ingresosList.add(ingresoEnEdicion);

            System.out.println("Ingreso actualizado en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al actualizar el ingreso en la base de datos.");
        }
    }

    private void guardarIngresoEnBaseDeDatos(Ingreso ingreso) {
        String query = "INSERT INTO ingresos (usuario_id, categoria_id, descripcion, monto, fecha_ingreso) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Obtener el ID de la categoría seleccionada
            int categoriaId = obtenerCategoriaId(ingreso.getCategoria());
            if (categoriaId == -1) {
                System.out.println("Error: Categoría no encontrada. Asegúrate de que la categoría exista en la base de datos.");
                return;
            }

            statement.setInt(1, Session.getUsuarioId());
            statement.setInt(2, categoriaId);
            statement.setString(3, ingreso.getDescripcion());
            statement.setDouble(4, ingreso.getMonto());
            statement.setDate(5, java.sql.Date.valueOf(ingreso.getFecha()));

            statement.executeUpdate();
            System.out.println("Ingreso guardado en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al guardar el ingreso en la base de datos.");
        }
    }

    private int obtenerCategoriaId(String categoria) {
        String query = "SELECT id_categorias AS id FROM categorias WHERE usuario_id = ? AND nombre = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Buscar en la tabla `categorias` para el usuario actual
            statement.setInt(1, Session.getUsuarioId());
            statement.setString(2, categoria);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }

            // Si no existe, crear una nueva categoría personalizada basada en `categorias_generales`
            return crearCategoriaPersonalizada(connection, categoria);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al obtener el ID de la categoría.");
        }
        return -1; // Retorna -1 si no se encuentra ni se puede crear la categoría
    }

    private int crearCategoriaPersonalizada(Connection connection, String categoria) {
        String queryInsert = "INSERT INTO categorias (usuario_id, nombre, tipo) " +
                "SELECT ?, nombre, tipo FROM categorias_generales WHERE nombre = ? RETURNING id_categorias";
        try (PreparedStatement statement = connection.prepareStatement(queryInsert)) {
            statement.setInt(1, Session.getUsuarioId());
            statement.setString(2, categoria);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int nuevaCategoriaId = resultSet.getInt("id_categorias");
                    System.out.println("Nueva categoría personalizada creada: " + categoria);
                    return nuevaCategoriaId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al crear una nueva categoría personalizada.");
        }
        return -1;
    }

    private void agregarBotones() {
        // Botón Editar
        colEditar.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editar");

            {
                editButton.setOnAction(event -> {
                    Ingreso ingreso = getTableView().getItems().get(getIndex());
                    editarIngreso(ingreso);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        // Botón Eliminar
        colEliminar.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Eliminar");

            {
                deleteButton.setOnAction(event -> {
                    Ingreso ingreso = getTableView().getItems().get(getIndex());
                    eliminarIngreso(ingreso);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void editarIngreso(Ingreso ingreso) {
        // Guardar el ingreso que se está editando
        ingresoEnEdicion = ingreso;

        // Rellenar los campos con los datos actuales del ingreso
        descripcionField.setText(ingreso.getDescripcion());
        categoriaComboBox.setValue(ingreso.getCategoria());
        montoField.setText(String.valueOf(ingreso.getMonto()));

        // Eliminar temporalmente el ingreso de la tabla
        ingresosList.remove(ingreso);
        System.out.println("Editando ingreso: " + ingreso.getDescripcion());
    }

    private void eliminarIngreso(Ingreso ingreso) {
        String query = "DELETE FROM ingresos WHERE descripcion = ? AND usuario_id = ? AND monto = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ingreso.getDescripcion());
            statement.setInt(2, Session.getUsuarioId());
            statement.setDouble(3, ingreso.getMonto());
            statement.executeUpdate();

            ingresosList.remove(ingreso);
            System.out.println("Ingreso eliminado: " + ingreso.getDescripcion());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al eliminar el ingreso.");
        }
    }

    private void abrirVentanaNuevaCategoria() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/categoria.fxml"));
            Parent root = loader.load();

            // Configurar el controlador de la nueva categoría
            CategoriaController categoriaController = loader.getController();
            categoriaController.setIngresosController(this); // Pasar referencia al controlador actual

            Stage stage = new Stage();
            stage.setTitle("Nueva Categoría");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al abrir la ventana de nueva categoría.");
        }
    }

    @FXML
    private void cancelarIngreso() {
        // Restablecer los campos del formulario
        descripcionField.clear();
        montoField.clear();
        categoriaComboBox.setValue(null);

        // Si había un ingreso en edición, cancelar la edición
        ingresoEnEdicion = null;

        System.out.println("Formulario restablecido.");
    }
}
