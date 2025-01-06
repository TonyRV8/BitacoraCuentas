package com.example.bitacoracuentas;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QuincenaController {

    @FXML
    private TextField saldoField;

    @FXML
    private TextField separarField;

    @FXML
    private Label sobranteLabel;

    @FXML
    private TextField ingresosPorcentaje;

    @FXML
    private TextField ingresosTotal;

    @FXML
    private TextField adeudosPorcentaje;

    @FXML
    private TextField adeudosTotal;

    @FXML
    private TextField deudasPorcentaje;

    @FXML
    private TextField deudasTotal;

    @FXML
    private TextField inversionesPorcentaje;

    @FXML
    private TextField inversionesTotal;

    @FXML
    private TextField presupuestosPorcentaje;

    @FXML
    private TextField presupuestosTotal;

    @FXML
    private Label aviso1Label;

    @FXML
    private Label aviso2Label;

    @FXML
    private Button guardarButton;

    private double saldoTotal = 0.0;
    private double montoSeparar = 0.0;

    @FXML
    public void initialize() {
        System.out.println("QuincenaController inicializado.");

        // Listener para el saldo total
        saldoField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                saldoTotal = Double.parseDouble(newValue.trim());
                if (saldoTotal < 0) {
                    aviso1Label.setText("El saldo total no puede ser negativo.");
                    saldoTotal = 0.0;
                    saldoField.setText("0.0");
                } else {
                    montoSeparar = 0.0; // Reinicia el monto a separar
                    separarField.setText("0.0");
                    actualizarSaldoSobrante();
                    aviso1Label.setText("");
                }
            } catch (NumberFormatException e) {
                saldoTotal = 0.0;
                aviso1Label.setText("Por favor, ingresa un saldo total válido.");
            }
        });

        // Listener para el monto a separar
        separarField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                montoSeparar = Double.parseDouble(newValue.trim());
                if (montoSeparar < 0) {
                    aviso1Label.setText("El monto a separar no puede ser negativo.");
                    montoSeparar = 0.0;
                    separarField.setText("0.0");
                } else if (montoSeparar > saldoTotal) {
                    aviso1Label.setText("El monto a separar no puede superar el saldo total.");
                    montoSeparar = 0.0;
                    separarField.setText("0.0");
                } else {
                    aviso1Label.setText("");
                }
                actualizarSaldoSobrante();
                calcularDistribucion();
            } catch (NumberFormatException e) {
                montoSeparar = 0.0;
                aviso1Label.setText("Por favor, ingresa un monto a separar válido.");
            }
        });

        // Configurar listeners para cada categoría
        configurarListenerPorcentajeYTotal(ingresosPorcentaje, ingresosTotal);
        configurarListenerPorcentajeYTotal(adeudosPorcentaje, adeudosTotal);
        configurarListenerPorcentajeYTotal(deudasPorcentaje, deudasTotal);
        configurarListenerPorcentajeYTotal(inversionesPorcentaje, inversionesTotal);
        configurarListenerPorcentajeYTotal(presupuestosPorcentaje, presupuestosTotal);

        guardarButton.setOnAction(event -> guardarDatos());

    }

    private void actualizarSaldoSobrante() {
        double sobrante = saldoTotal - montoSeparar;
        sobranteLabel.setText(String.format("%.2f", sobrante));
    }

    private void configurarListenerPorcentajeYTotal(TextField porcentajeField, TextField totalField) {
        // Listener para el porcentaje
        porcentajeField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double porcentaje = Double.parseDouble(newValue.trim());
                if (porcentaje < 0 || porcentaje > 100) {
                    aviso2Label.setText("Los porcentajes deben estar entre 0 y 100.");
                    return;
                }
                aviso2Label.setText("");
                // Calcula el total basado en el porcentaje
                double total = montoSeparar * (porcentaje / 100);
                totalField.setText(String.format("%.2f", total));
                validarSumaPorcentajes();
            } catch (NumberFormatException e) {
                totalField.setText("0.0");
                aviso2Label.setText("Por favor, ingresa valores válidos en los porcentajes.");
            }
        });

        // Listener para el total
        totalField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double total = Double.parseDouble(newValue.trim());
                if (total < 0 || total > montoSeparar) {
                    aviso2Label.setText("Los totales no pueden ser negativos ni mayores al monto a separar.");
                    return;
                }
                aviso2Label.setText("");
                // Calcula el porcentaje basado en el total
                double porcentaje = (total / montoSeparar) * 100;
                porcentajeField.setText(String.format("%.2f", porcentaje));
                validarSumaPorcentajes();
            } catch (NumberFormatException e) {
                porcentajeField.setText("0.0");
                aviso2Label.setText("Por favor, ingresa valores válidos en los totales.");
            }
        });
    }

    private void validarSumaPorcentajes() {
        try {
            double sumaPorcentajes = Double.parseDouble(ingresosPorcentaje.getText().trim())
                    + Double.parseDouble(adeudosPorcentaje.getText().trim())
                    + Double.parseDouble(deudasPorcentaje.getText().trim())
                    + Double.parseDouble(inversionesPorcentaje.getText().trim())
                    + Double.parseDouble(presupuestosPorcentaje.getText().trim());

            if (sumaPorcentajes > 100) {
                aviso2Label.setText("La suma de los porcentajes no puede superar el 100%.");
            } else {
                aviso2Label.setText("");
            }
        } catch (NumberFormatException e) {
            aviso2Label.setText("Error al validar porcentajes: revisa los campos.");
        }
    }

    private void calcularDistribucion() {
        calcularMontoCategoria(ingresosPorcentaje, ingresosTotal);
        calcularMontoCategoria(adeudosPorcentaje, adeudosTotal);
        calcularMontoCategoria(deudasPorcentaje, deudasTotal);
        calcularMontoCategoria(inversionesPorcentaje, inversionesTotal);
        calcularMontoCategoria(presupuestosPorcentaje, presupuestosTotal);
    }

    private void calcularMontoCategoria(TextField porcentajeField, TextField totalField) {
        try {
            double porcentaje = Double.parseDouble(porcentajeField.getText().trim());
            double montoAsignado = montoSeparar * (porcentaje / 100);
            totalField.setText(String.format("%.2f", montoAsignado));
        } catch (NumberFormatException e) {
            totalField.setText("0.0");
        }
    }

    @FXML
    private void guardarDatos() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Validar que no existan errores antes de guardar
            if (!aviso1Label.getText().isEmpty() || !aviso2Label.getText().isEmpty()) {
                System.out.println("Corrige los errores antes de guardar.");
                return;
            }

            // Verificar si ya existen registros para este usuario en saldo_quincena
            String verificarQuery = "SELECT COUNT(*) FROM saldo_quincena WHERE usuario_id = ?";
            boolean registroExistente = false;

            try (PreparedStatement verificarStatement = connection.prepareStatement(verificarQuery)) {
                verificarStatement.setInt(1, Session.getUsuarioId()); // Obtén el usuario actual
                ResultSet resultSet = verificarStatement.executeQuery();
                if (resultSet.next()) {
                    registroExistente = resultSet.getInt(1) > 0; // Si el conteo es mayor a 0, ya existe un registro
                }
            }

            if (registroExistente) {
                // Realizar un UPDATE si ya existen registros para este usuario
                String updateQuery = "UPDATE saldo_quincena SET saldo = ?, monto_a_separar = ?, saldo_sobrante = ? WHERE usuario_id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setDouble(1, saldoTotal);
                    updateStatement.setDouble(2, montoSeparar);
                    updateStatement.setDouble(3, saldoTotal - montoSeparar);
                    updateStatement.setInt(4, Session.getUsuarioId());
                    updateStatement.executeUpdate();
                    System.out.println("Registro actualizado en saldo_quincena.");
                }
            } else {
                // Realizar un INSERT si no existen registros para este usuario
                String insertQuery = "INSERT INTO saldo_quincena (usuario_id, saldo, monto_a_separar, saldo_sobrante) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, Session.getUsuarioId());
                    insertStatement.setDouble(2, saldoTotal);
                    insertStatement.setDouble(3, montoSeparar);
                    insertStatement.setDouble(4, saldoTotal - montoSeparar);
                    insertStatement.executeUpdate();
                    System.out.println("Nuevo registro insertado en saldo_quincena.");
                }
            }

            // Guardar las asignaciones en configuracion_asignaciones
            String queryAsignaciones = "INSERT INTO configuracion_asignaciones (usuario_id, modulo, porcentaje) " +
                    "VALUES (?, ?, ?) ON CONFLICT (usuario_id, modulo) DO UPDATE SET porcentaje = EXCLUDED.porcentaje";
            try (PreparedStatement statementAsignaciones = connection.prepareStatement(queryAsignaciones)) {
                guardarAsignacion(statementAsignaciones, "Ingresos", ingresosPorcentaje);
                guardarAsignacion(statementAsignaciones, "Adeudos", adeudosPorcentaje);
                guardarAsignacion(statementAsignaciones, "Deudas", deudasPorcentaje);
                guardarAsignacion(statementAsignaciones, "Inversiones", inversionesPorcentaje);
                guardarAsignacion(statementAsignaciones, "Presupuestos", presupuestosPorcentaje);
            }


            System.out.println("Datos guardados exitosamente en la base de datos.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al guardar en la base de datos.");
        }
    }



    private void guardarAsignacion(PreparedStatement statement, String modulo, TextField porcentajeField) throws Exception {
        double porcentaje = Double.parseDouble(porcentajeField.getText().trim());
        if (porcentaje > 0) {
            statement.setInt(1, Session.getUsuarioId()); // Usuario actual
            statement.setString(2, modulo);
            statement.setDouble(3, porcentaje);
            statement.executeUpdate();
        }
    }







}
