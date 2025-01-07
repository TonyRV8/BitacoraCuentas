package com.example.bitacoracuentas;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExportController {

    public RadioButton reporteRadioButton;
    public RadioButton datosRadioButton;
    @FXML
    private ComboBox<String> periodoComboBox;
    @FXML
    private RadioButton pdfRadioButton;
    @FXML
    private RadioButton excelRadioButton;
    @FXML
    private RadioButton csvRadioButton;
    @FXML
    private CheckBox reporteCheckBox;
    @FXML
    private CheckBox datosCheckBox;
    @FXML
    private Button exportarButton;

    @FXML
    private ToggleGroup archivoToggleGroup;

    @FXML
    private ToggleGroup formatoToggleGroup;

    @FXML
    public void initialize() {
        // Inicialización para asegurar que los ToggleGroup estén bien configurados
        archivoToggleGroup = new ToggleGroup();
        formatoToggleGroup = new ToggleGroup();

        pdfRadioButton.setToggleGroup(archivoToggleGroup);
        excelRadioButton.setToggleGroup(archivoToggleGroup);
        csvRadioButton.setToggleGroup(archivoToggleGroup);

        reporteRadioButton.setToggleGroup(formatoToggleGroup);
        datosRadioButton.setToggleGroup(formatoToggleGroup);
        cargarPeriodos();
    }

    private void cargarPeriodos() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT MIN(fecha) AS fecha_minima FROM ( SELECT fecha_creacion AS fecha FROM presupuestos WHERE usuario_id = ? UNION ALL SELECT fecha_inicio AS fecha FROM inversiones WHERE usuario_id = ? UNION ALL SELECT fecha_vencimiento AS fecha FROM adeudos WHERE usuario_id = ? UNION ALL SELECT fecha_ingreso AS fecha FROM ingresos WHERE usuario_id = ?) AS fechas";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Session.getUsuarioId());
            statement.setInt(2, Session.getUsuarioId());
            statement.setInt(3, Session.getUsuarioId());
            statement.setInt(4, Session.getUsuarioId());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                LocalDate fechaMinima = resultSet.getDate("fecha_minima").toLocalDate();
                ObservableList<String> periodos = generarPeriodosDesde(fechaMinima);
                periodoComboBox.setItems(periodos);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<String> generarPeriodosDesde(LocalDate fechaMinima) {
        List<String> periodos = new ArrayList<>();
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        while (!fechaActual.isBefore(fechaMinima)) {
            YearMonth mes = YearMonth.from(fechaActual);
            periodos.add("2da Quincena " + mes.format(formatter));
            periodos.add("1ra Quincena " + mes.format(formatter));
            fechaActual = fechaActual.minusMonths(1);
        }

        return FXCollections.observableArrayList(periodos);
    }

    @FXML
    private void exportarDatos() {
        String periodoSeleccionado = periodoComboBox.getValue();
        RadioButton archivoSeleccionado = (RadioButton) archivoToggleGroup.getSelectedToggle();
        RadioButton formatoSeleccionado = (RadioButton) formatoToggleGroup.getSelectedToggle(); // Se obtiene el RadioButton seleccionado para formato

        if (periodoSeleccionado == null || archivoSeleccionado == null || formatoSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, complete todas las selecciones antes de exportar.");
            alert.showAndWait();
            return;
        }

        // Crear una instancia de ExportarDatos y llamar al método exportarDatos
        ExportarDatos exportarDatos = new ExportarDatos();
        exportarDatos.exportarDatos(periodoSeleccionado, archivoSeleccionado, formatoSeleccionado);

        System.out.println("Exportando " + archivoSeleccionado.getText() + " para el periodo: " + periodoSeleccionado);
    }

}
