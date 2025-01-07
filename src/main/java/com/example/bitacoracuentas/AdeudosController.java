package com.example.bitacoracuentas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class AdeudosController {

    @FXML
    private TextField descripcionField;

    @FXML
    private TextField montoField;

    @FXML
    private ComboBox<String> categoriaComboBox;

    @FXML
    private TextField fechaVencimientoField;

    @FXML
    private TableView<Adeudo> adeudosTable;

    @FXML
    private TableColumn<Adeudo, String> colDescripcion;

    @FXML
    private TableColumn<Adeudo, Double> colMonto;

    @FXML
    private TableColumn<Adeudo, Double> colSaldoRestante;

    @FXML
    private TableColumn<Adeudo, String> colCategoria;

    @FXML
    private TableColumn<Adeudo, LocalDate> colFechaVencimiento;

    @FXML
    private TableColumn<Adeudo, String> colEstado;

    @FXML
    private TableColumn<Adeudo, Void> colEditar;

    @FXML
    private TableColumn<Adeudo, Void> colEliminar;

    @FXML
    private Button guardarAdeudoButton;

    @FXML
    private Button cancelarAdeudoButton;

    @FXML
    private Button nuevaCategoriaButton;

    @FXML
    private Label avisoLabel; // Nuevo label para mostrar mensajes


    private ObservableList<Adeudo> adeudosList;

    private Adeudo adeudoEnEdicion = null;

    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colSaldoRestante.setCellValueFactory(new PropertyValueFactory<>("saldoRestante"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colFechaVencimiento.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Inicializar la lista de adeudos
        adeudosList = FXCollections.observableArrayList();
        adeudosTable.setItems(adeudosList);

        // Cargar categorías y adeudos existentes
        cargarCategorias();
        cargarAdeudos();

        // Configurar botones de editar y eliminar
        agregarBotones();

        cancelarAdeudoButton.setOnAction(event -> cancelarAdeudo());
        guardarAdeudoButton.setOnAction(event -> guardarAdeudo());
        nuevaCategoriaButton.setOnAction(event -> abrirVentanaNuevaCategoria());
    }

    protected void cargarCategorias() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            categoriaComboBox.getItems().clear();

            // Cargar categorías de adeudos personalizadas
            String query = "SELECT nombre FROM categorias WHERE usuario_id = ? AND tipo = 'Adeudos'";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, Session.getUsuarioId());
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        categoriaComboBox.getItems().add(resultSet.getString("nombre"));
                    }
                }
            }

            System.out.println("Categorías de adeudos cargadas correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar las categorías de adeudos.");
        }
    }

    protected void cargarAdeudos() {
        String query = "SELECT a.descripcion, a.monto, a.saldo_restante, c.nombre AS categoria, a.fecha_vencimiento, a.estado " +
                "FROM adeudos a " +
                "JOIN categorias c ON a.categoria_id = c.id_categorias " +
                "WHERE a.usuario_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Session.getUsuarioId());
            try (ResultSet resultSet = statement.executeQuery()) {
                adeudosList.clear();
                while (resultSet.next()) {
                    String descripcion = resultSet.getString("descripcion");
                    double monto = resultSet.getDouble("monto");
                    double saldoRestante = resultSet.getDouble("saldo_restante");
                    String categoria = resultSet.getString("categoria");
                    LocalDate fechaVencimiento = resultSet.getDate("fecha_vencimiento") != null
                            ? resultSet.getDate("fecha_vencimiento").toLocalDate()
                            : null;
                    String estado = resultSet.getString("estado");

                    Adeudo adeudo = new Adeudo(descripcion, monto, saldoRestante, categoria, fechaVencimiento, estado);
                    adeudosList.add(adeudo);
                }
            }
            System.out.println("Adeudos cargados correctamente.");
            setAviso("Adeudos cargados correctamente.", "success");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar los adeudos.");
        }
    }

    @FXML
    private void guardarAdeudo() {
        try {
            String descripcion = descripcionField.getText();
            String categoria = categoriaComboBox.getValue();
            double monto = Double.parseDouble(montoField.getText());
            String fechaVencimientoStr = fechaVencimientoField.getText();

            if (descripcion.isEmpty() || categoria == null || monto <= 0) {
                System.out.println("Por favor, completa todos los campos correctamente.");
                return;
            }

            LocalDate fechaVencimiento = !fechaVencimientoStr.isEmpty()
                    ? LocalDate.parse(fechaVencimientoStr)
                    : null;

            if (adeudoEnEdicion != null) {
                // Actualizar en la base de datos
                actualizarAdeudoEnBaseDeDatos(descripcion, categoria, monto, fechaVencimiento);

                // Actualizar directamente el objeto en edición
                adeudoEnEdicion.setDescripcion(descripcion);
                adeudoEnEdicion.setCategoria(categoria);
                adeudoEnEdicion.setMonto(monto);
                adeudoEnEdicion.setSaldoRestante(monto);
                adeudoEnEdicion.setFechaVencimiento(fechaVencimiento);

                // Refrescar la tabla para reflejar los cambios
                adeudosTable.refresh();

                System.out.println("Adeudo editado: " + adeudoEnEdicion);
            } else {
                // Crear un nuevo adeudo
                Adeudo nuevoAdeudo = new Adeudo(descripcion, monto, monto, categoria, fechaVencimiento, "pendiente");
                adeudosList.add(nuevoAdeudo);
                guardarAdeudoEnBaseDeDatos(nuevoAdeudo);
            }

            // Limpiar campos y restablecer variables
            descripcionField.clear();
            montoField.clear();
            fechaVencimientoField.clear();
            categoriaComboBox.setValue(null);
            adeudoEnEdicion = null;

            setAviso("Adeudo guardado correctamente.", "success");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Por favor, completa todos los campos correctamente.");
            setAviso("Completa todos los campos correctamente.", "error");

        }
    }






    private void guardarAdeudoEnBaseDeDatos(Adeudo adeudo) {
        String query = "INSERT INTO adeudos (usuario_id, categoria_id, descripcion, monto, saldo_restante, fecha_vencimiento, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            int categoriaId = obtenerCategoriaId(adeudo.getCategoria());
            if (categoriaId == -1) {
                System.out.println("Error: Categoría no encontrada.");
                return;
            }

            statement.setInt(1, Session.getUsuarioId());
            statement.setInt(2, categoriaId);
            statement.setString(3, adeudo.getDescripcion());
            statement.setDouble(4, adeudo.getMonto());
            statement.setDouble(5, adeudo.getSaldoRestante());
            statement.setDate(6, adeudo.getFechaVencimiento() != null
                    ? java.sql.Date.valueOf(adeudo.getFechaVencimiento())
                    : null);
            statement.setString(7, adeudo.getEstado());

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al guardar el adeudo en la base de datos.");
            setAviso("Error al guardar el adeudo.", "error");

        }
    }

    private void actualizarAdeudoEnBaseDeDatos(String nuevaDescripcion, String nuevaCategoria, double nuevoMonto, LocalDate nuevaFechaVencimiento) {
        String query = "UPDATE adeudos SET descripcion = ?, categoria_id = ?, monto = ?, saldo_restante = ?, fecha_vencimiento = ? " +
                "WHERE descripcion = ? AND usuario_id = ? AND monto = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Obtener el ID de la nueva categoría
            int nuevaCategoriaId = obtenerCategoriaId(nuevaCategoria);
            if (nuevaCategoriaId == -1) {
                System.out.println("Error: Categoría no encontrada.");
                return;
            }

            // Configurar los valores para la actualización
            statement.setString(1, nuevaDescripcion);
            statement.setInt(2, nuevaCategoriaId);
            statement.setDouble(3, nuevoMonto);
            statement.setDouble(4, nuevoMonto); // Saldo restante igual al monto
            statement.setDate(5, nuevaFechaVencimiento != null ? java.sql.Date.valueOf(nuevaFechaVencimiento) : null);

            // Configurar los valores originales para encontrar el registro
            statement.setString(6, adeudoEnEdicion.getDescripcion());
            statement.setInt(7, Session.getUsuarioId());
            statement.setDouble(8, adeudoEnEdicion.getMonto());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Adeudo actualizado en la base de datos.");
                setAviso("Adeudo actualizado", "success");

            } else {
                System.out.println("No se encontró el registro para actualizar.");
                setAviso("No se encontró el registro para actualizar.", "error");

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al actualizar el adeudo en la base de datos.");
            setAviso("Error al actualizar adeudo.", "error");

        }
    }



    private void agregarBotones() {
        // Botón Editar
        colEditar.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editar");

            {
                editButton.setOnAction(event -> {
                    Adeudo adeudo = getTableView().getItems().get(getIndex());
                    editarAdeudo(adeudo);
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
                    Adeudo adeudo = getTableView().getItems().get(getIndex());
                    eliminarAdeudo(adeudo);
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

    private void editarAdeudo(Adeudo adeudo) {
        // Guardar el adeudo que se está editando
        adeudoEnEdicion = adeudo;

        // Rellenar los campos del formulario con los datos actuales del adeudo
        descripcionField.setText(adeudo.getDescripcion());
        categoriaComboBox.setValue(adeudo.getCategoria());
        montoField.setText(String.valueOf(adeudo.getMonto()));
        fechaVencimientoField.setText(adeudo.getFechaVencimiento() != null
                ? adeudo.getFechaVencimiento().toString()
                : "");

        // Imprimir el adeudo en consola para verificar datos
        System.out.println("Editando adeudo: " + adeudo);
        setAviso("Editando adeudo: " + adeudo.getDescripcion(), "info");

        // No elimines el registro de la lista si no es estrictamente necesario
    }


    private void eliminarAdeudo(Adeudo adeudo) {
        String query = "DELETE FROM adeudos WHERE descripcion = ? AND usuario_id = ? AND monto = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, adeudo.getDescripcion());
            statement.setInt(2, Session.getUsuarioId());
            statement.setDouble(3, adeudo.getMonto());
            statement.executeUpdate();

            adeudosList.remove(adeudo);
            System.out.println("Adeudo eliminado: " + adeudo.getDescripcion());
            setAviso("Adeudo eliminado: " + adeudo.getDescripcion(), "success");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al eliminar el adeudo.");
            setAviso("Error al eliminar el adeudo.", "error");

        }
    }




    private int obtenerCategoriaId(String categoria) {
        String query = "SELECT id_categorias FROM categorias WHERE usuario_id = ? AND nombre = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Session.getUsuarioId());
            statement.setString(2, categoria);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_categorias");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Retorna -1 si no se encuentra la categoría
    }



    private void abrirVentanaNuevaCategoria() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bitacoracuentas/categoria.fxml"));
            Parent root = loader.load();

            // Configurar el controlador de la nueva categoría
            CategoriaController categoriaController = loader.getController();
            categoriaController.setTipoCategoria("Adeudos"); // Fijar el tipo de categoría
            categoriaController.setAdeudosController(this);  // Pasar referencia al controlador actual

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
    private void cancelarAdeudo() {
        // Restablecer los campos del formulario
        descripcionField.clear();
        montoField.clear();
        fechaVencimientoField.clear();
        categoriaComboBox.setValue(null);

        // Si había un adeudo en edición, cancelar la edición
        adeudoEnEdicion = null;

        System.out.println("Formulario restablecido.");
        setAviso("Formulario restablecido.", "info");

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
