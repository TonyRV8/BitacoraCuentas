package com.example.bitacoracuentas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class DeudasController {

    @FXML
    private TextField acreedorField;

    @FXML
    private TextField montoField;

    @FXML
    private TextField tasaInteresField;

    @FXML
    private TextField fechaPagoField;

    @FXML
    private TableView<Deuda> deudasTable;

    @FXML
    private TableColumn<Deuda, String> colAcreedor;

    @FXML
    private TableColumn<Deuda, Double> colMonto;

    @FXML
    private TableColumn<Deuda, Double> colSaldoRestante;

    @FXML
    private TableColumn<Deuda, Double> colTasaInteres;

    @FXML
    private TableColumn<Deuda, LocalDate> colFechaPago;

    @FXML
    private TableColumn<Deuda, String> colEstado;

    @FXML
    private TableColumn<Deuda, Void> colEditar;

    @FXML
    private TableColumn<Deuda, Void> colEliminar;

    @FXML
    private Button guardarDeudaButton;

    @FXML
    private Button cancelarDeudaButton;

    @FXML
    private Label avisoLabel; // Nuevo label para mensajes

    private ObservableList<Deuda> deudasList;
    private Deuda deudaEnEdicion = null;

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colAcreedor.setCellValueFactory(new PropertyValueFactory<>("acreedor"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colSaldoRestante.setCellValueFactory(new PropertyValueFactory<>("saldoRestante"));
        colTasaInteres.setCellValueFactory(new PropertyValueFactory<>("tasaInteres"));
        colFechaPago.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Inicializar lista de deudas
        deudasList = FXCollections.observableArrayList();
        deudasTable.setItems(deudasList);

        // Cargar deudas existentes
        cargarDeudas();

        // Configurar botones de editar y eliminar
        agregarBotones();

        // Configurar acciones de los botones
        guardarDeudaButton.setOnAction(event -> guardarDeuda());
        cancelarDeudaButton.setOnAction(event -> cancelarDeuda());
    }

    private void cargarDeudas() {
        String query = "SELECT acreedor, monto, saldo_restante, tasa_interes, fecha_pago, estado " +
                "FROM deudas WHERE usuario_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Session.getUsuarioId());
            try (ResultSet resultSet = statement.executeQuery()) {
                deudasList.clear();
                while (resultSet.next()) {
                    String acreedor = resultSet.getString("acreedor");
                    double monto = resultSet.getDouble("monto");
                    double saldoRestante = resultSet.getDouble("saldo_restante");
                    double tasaInteres = resultSet.getDouble("tasa_interes");
                    LocalDate fechaPago = resultSet.getDate("fecha_pago").toLocalDate();
                    String estado = resultSet.getString("estado");

                    Deuda deuda = new Deuda(acreedor, monto, saldoRestante, tasaInteres, fechaPago, estado);
                    deudasList.add(deuda);
                }
                setAviso("Deudas cargadas correctamente.", "success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            setAviso("Error al cargar las deudas.", "error");
        }
    }

    @FXML
    private void guardarDeuda() {
        try {
            String acreedor = acreedorField.getText();
            double monto = Double.parseDouble(montoField.getText());
            double tasaInteres = Double.parseDouble(tasaInteresField.getText());
            String fechaPagoStr = fechaPagoField.getText();

            if (acreedor.isEmpty() || monto <= 0 || tasaInteres < 0 || fechaPagoStr.isEmpty()) {
                setAviso("Por favor, completa todos los campos correctamente.", "warning");
                return;
            }

            LocalDate fechaPago = LocalDate.parse(fechaPagoStr);

            if (deudaEnEdicion != null) {
                // Actualizamos la deuda existente
                actualizarDeudaEnBaseDeDatos(acreedor, monto, tasaInteres, fechaPago);

                // Actualizar el objeto en edición
                deudaEnEdicion.setAcreedor(acreedor);
                deudaEnEdicion.setMonto(monto);
                deudaEnEdicion.setSaldoRestante(monto);
                deudaEnEdicion.setTasaInteres(tasaInteres);
                deudaEnEdicion.setFechaPago(fechaPago);

                deudasTable.refresh();
                setAviso("Deuda editada correctamente.", "success");
            } else {
                // Crear una nueva deuda con estado "pendiente"
                Deuda nuevaDeuda = new Deuda(acreedor, monto, monto, tasaInteres, fechaPago, "pendiente");
                deudasList.add(nuevaDeuda);
                guardarDeudaEnBaseDeDatos(nuevaDeuda);
                setAviso("Deuda guardada correctamente.", "success");
            }

            // Limpiar campos
            limpiarCampos();
        } catch (NumberFormatException e) {
            setAviso("Por favor, ingresa valores válidos.", "warning");
        } catch (Exception e) {
            e.printStackTrace();
            setAviso("Error al guardar la deuda.", "error");
        }
    }

    private void actualizarDeudaEnBaseDeDatos(String acreedor, double monto, double tasaInteres, LocalDate fechaPago) {
        String query = "UPDATE deudas SET acreedor = ?, monto = ?, saldo_restante = ?, tasa_interes = ?, fecha_pago = ? " +
                "WHERE acreedor = ? AND usuario_id = ? AND monto = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, acreedor);
            statement.setDouble(2, monto);
            statement.setDouble(3, monto); // Saldo restante igual al monto
            statement.setDouble(4, tasaInteres);
            statement.setDate(5, java.sql.Date.valueOf(fechaPago));

            // Parámetros originales para encontrar el registro
            statement.setString(6, deudaEnEdicion.getAcreedor());
            statement.setInt(7, Session.getUsuarioId());
            statement.setDouble(8, deudaEnEdicion.getMonto());

            statement.executeUpdate();
            setAviso("Deuda actualizada en la base de datos.", "success");
        } catch (Exception e) {
            e.printStackTrace();
            setAviso("Error al actualizar la deuda en la base de datos.", "error");
        }
    }

    private void guardarDeudaEnBaseDeDatos(Deuda deuda) {
        String query = "INSERT INTO deudas (usuario_id, acreedor, monto, saldo_restante, tasa_interes, fecha_pago, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, Session.getUsuarioId());
            statement.setString(2, deuda.getAcreedor());
            statement.setDouble(3, deuda.getMonto());
            statement.setDouble(4, deuda.getSaldoRestante());
            statement.setDouble(5, deuda.getTasaInteres());
            statement.setDate(6, java.sql.Date.valueOf(deuda.getFechaPago()));
            statement.setString(7, deuda.getEstado());

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            setAviso("Error al guardar la deuda en la base de datos.", "error");
        }
    }

    private void agregarBotones() {
        // Botón Editar
        colEditar.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Editar");

            {
                editButton.setOnAction(event -> {
                    Deuda deuda = getTableView().getItems().get(getIndex());
                    editarDeuda(deuda);
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
                    Deuda deuda = getTableView().getItems().get(getIndex());
                    eliminarDeuda(deuda);
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

    private void editarDeuda(Deuda deuda) {
        deudaEnEdicion = deuda;

        acreedorField.setText(deuda.getAcreedor());
        montoField.setText(String.valueOf(deuda.getMonto()));
        tasaInteresField.setText(String.valueOf(deuda.getTasaInteres()));
        fechaPagoField.setText(deuda.getFechaPago().toString());

        setAviso("Editando deuda: " + deuda.getAcreedor(), "info");
    }

    private void eliminarDeuda(Deuda deuda) {
        String query = "DELETE FROM deudas WHERE acreedor = ? AND usuario_id = ? AND monto = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, deuda.getAcreedor());
            statement.setInt(2, Session.getUsuarioId());
            statement.setDouble(3, deuda.getMonto());
            statement.executeUpdate();

            deudasList.remove(deuda);
            setAviso("Deuda eliminada: " + deuda.getAcreedor(), "success");
        } catch (Exception e) {
            e.printStackTrace();
            setAviso("Error al eliminar la deuda.", "error");
        }
    }

    private void cancelarDeuda() {
        limpiarCampos();
        deudaEnEdicion = null;
        setAviso("Formulario restablecido.", "info");
    }

    private void limpiarCampos() {
        acreedorField.clear();
        montoField.clear();
        tasaInteresField.clear();
        fechaPagoField.clear();
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
