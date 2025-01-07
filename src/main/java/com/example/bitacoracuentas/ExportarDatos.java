package com.example.bitacoracuentas;

import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.stage.FileChooser;
import jdk.internal.foreign.SystemLookup;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;  // Importación correcta

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;

import com.itextpdf.text.pdf.PdfPTable;



import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class ExportarDatos {

    public void exportarDatos(String periodoSeleccionado, RadioButton archivoSeleccionado, RadioButton formatoSeleccionado) {
        // Verifica que se haya seleccionado periodo, formato y tipo de exportación (Reporte o Datos)
        if (periodoSeleccionado == null || archivoSeleccionado == null || formatoSeleccionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Por favor, complete todas las selecciones antes de exportar.");
            alert.showAndWait();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            List<String> tablas = Arrays.asList("presupuestos", "saldo_quincena", "inversiones", "adeudos", "ingresos");

            // Extraer solo el mes y año de la cadena
            String[] partesPeriodo = periodoSeleccionado.split(" ");
            String mesAnio = partesPeriodo[partesPeriodo.length - 2] + " " + partesPeriodo[partesPeriodo.length - 1];

            // Convertir la cadena 'enero 2025' a YearMonth
            YearMonth periodo = YearMonth.parse(mesAnio, DateTimeFormatter.ofPattern("MMMM yyyy"));

            LocalDate fechaInicio = null;
            LocalDate fechaFin = periodo.atEndOfMonth();

            // Determinar si es la primera o segunda quincena
            if (periodoSeleccionado.contains("1ra Quincena")) {
                fechaInicio = periodo.atDay(1); // 1 al 15
                fechaFin = periodo.atDay(15);
            } else if (periodoSeleccionado.contains("2da Quincena")) {
                fechaInicio = periodo.atDay(16); // 16 al final del mes
                fechaFin = periodo.atEndOfMonth();
            }

            // Verifica el formato seleccionado (CSV o Excel) y llama a la función correspondiente
            if (archivoSeleccionado.getText().equalsIgnoreCase("CSV")) {
                exportarCSV(connection, tablas, fechaInicio, fechaFin);
            } else if (archivoSeleccionado.getText().equalsIgnoreCase("Excel")) {
                exportarExcel(connection, tablas, fechaInicio, fechaFin);
            } else if (archivoSeleccionado.getText().equalsIgnoreCase("PDF")){
            exportarPDF(connection, tablas, fechaInicio, fechaFin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportarCSV(Connection connection, List<String> tablas, LocalDate fechaInicio, LocalDate fechaFin) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("exportacion_datos.csv");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String tabla : tablas) {
                    String query = "SELECT * FROM " + tabla;

                    if (tabla.equals("presupuestos")) {
                        query += " WHERE fecha_fin BETWEEN ? AND ?";
                    } else if (tabla.equals("saldo_quincena")) {
                        query += " WHERE usuario_id IS NOT NULL";
                    } else if (tabla.equals("inversiones")) {
                        query += " WHERE fecha_fin BETWEEN ? AND ?";
                    } else if (tabla.equals("adeudos")) {
                        query += " WHERE fecha_vencimiento BETWEEN ? AND ?";
                    } else if (tabla.equals("ingresos")) {
                        query += " WHERE fecha_ingreso BETWEEN ? AND ?";
                    }

                    try (PreparedStatement stmt = connection.prepareStatement(query)) {
                        if (!tabla.equals("saldo_quincena")) {
                            stmt.setDate(1, Date.valueOf(fechaInicio));
                            stmt.setDate(2, Date.valueOf(fechaFin));
                        }

                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs != null && rs.next()) {
                                int columnCount = rs.getMetaData().getColumnCount();
                                if (columnCount == 0) {
                                    throw new SQLException("La consulta no ha devuelto ninguna columna.");
                                }

                                // Escribir los encabezados de las columnas
                                StringBuilder header = new StringBuilder();
                                for (int i = 1; i <= columnCount; i++) {
                                    header.append(rs.getMetaData().getColumnName(i));
                                    if (i < columnCount) header.append(",");
                                }
                                writer.write(header.toString());
                                writer.newLine();

                                // Escribir los datos de cada fila
                                do {
                                    StringBuilder row = new StringBuilder();
                                    for (int i = 1; i <= columnCount; i++) {
                                        row.append(rs.getString(i));
                                        if (i < columnCount) row.append(",");
                                    }
                                    writer.write(row.toString());
                                    writer.newLine();
                                } while (rs.next());
                            } else {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "No se encontraron datos para el rango de fechas seleccionado.");
                                alert.showAndWait();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al ejecutar la consulta: " + e.getMessage());
                            alert.showAndWait();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Error al preparar la consulta: " + e.getMessage());
                        alert.showAndWait();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportarExcel(Connection connection, List<String> tablas, LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("exportacion_datos.xlsx");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fileOut = new FileOutputStream(file)) {

                for (String tabla : tablas) {
                    Sheet sheet = workbook.createSheet(tabla);
                    String query = "SELECT * FROM " + tabla;

                    if (tabla.equals("presupuestos")) {
                        query += " WHERE fecha_fin BETWEEN ? AND ?";
                    } else if (tabla.equals("saldo_quincena")) {
                        query += " WHERE usuario_id IS NOT NULL";
                    } else if (tabla.equals("inversiones")) {
                        query += " WHERE fecha_fin BETWEEN ? AND ?";
                    } else if (tabla.equals("adeudos")) {
                        query += " WHERE fecha_vencimiento BETWEEN ? AND ?";
                    } else if (tabla.equals("ingresos")) {
                        query += " WHERE fecha_ingreso BETWEEN ? AND ?";
                    }

                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        if (!tabla.equals("saldo_quincena")) {
                            statement.setDate(1, Date.valueOf(fechaInicio));
                            statement.setDate(2, Date.valueOf(fechaFin));
                        }

                        try (ResultSet resultSet = statement.executeQuery()) {
                            if (resultSet != null) {
                                Row headerRow = sheet.createRow(0);
                                int columnCount = resultSet.getMetaData().getColumnCount();

                                for (int i = 1; i <= columnCount; i++) {
                                    headerRow.createCell(i - 1).setCellValue(resultSet.getMetaData().getColumnName(i));
                                }

                                int rowNum = 1;
                                while (resultSet.next()) {
                                    Row row = sheet.createRow(rowNum++);
                                    for (int i = 1; i <= columnCount; i++) {
                                        row.createCell(i - 1).setCellValue(resultSet.getString(i));
                                    }
                                }
                            } else {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "No se encontraron datos para el rango de fechas seleccionado.");
                                alert.showAndWait();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Error al ejecutar la consulta: " + e.getMessage());
                        alert.showAndWait();
                    }
                }
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void exportarPDF(Connection connection, List<String> tablas, LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("exportacion_datos.pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                Document document = new Document();
                // Correcta inicialización de PdfWriter
                PdfWriter.getInstance(document, fileOut);
                document.open();

                for (String tabla : tablas) {
                    // Agregar el nombre de la tabla como título
                    Paragraph titulo = new Paragraph("Tabla: " + tabla, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
                    titulo.setAlignment(Element.ALIGN_CENTER);
                    document.add(titulo);

                    String query = "SELECT * FROM " + tabla;

                    if (tabla.equals("presupuestos")) {
                        query += " WHERE fecha_fin BETWEEN ? AND ?";
                    } else if (tabla.equals("saldo_quincena")) {
                        query += " WHERE usuario_id IS NOT NULL";
                    } else if (tabla.equals("inversiones")) {
                        query += " WHERE fecha_fin BETWEEN ? AND ?";
                    } else if (tabla.equals("adeudos")) {
                        query += " WHERE fecha_vencimiento BETWEEN ? AND ?";
                    } else if (tabla.equals("ingresos")) {
                        query += " WHERE fecha_ingreso BETWEEN ? AND ?";
                    }

                    try (PreparedStatement stmt = connection.prepareStatement(query)) {
                        if (!tabla.equals("saldo_quincena")) {
                            stmt.setDate(1, Date.valueOf(fechaInicio));
                            stmt.setDate(2, Date.valueOf(fechaFin));
                        }

                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs != null && rs.next()) {
                                int columnCount = rs.getMetaData().getColumnCount();

                                // Crear una tabla PDF
                                PdfPTable table = new PdfPTable(columnCount);
                                // Establecer el ancho de las columnas
                                table.setWidthPercentage(100);
                                float[] columnWidths = new float[columnCount];
                                Arrays.fill(columnWidths, 1f);
                                table.setWidths(columnWidths);

                                // Agregar los encabezados de las columnas
                                for (int i = 1; i <= columnCount; i++) {
                                    table.addCell(rs.getMetaData().getColumnName(i));
                                }

                                // Agregar los datos de las filas
                                do {
                                    for (int i = 1; i <= columnCount; i++) {
                                        table.addCell(rs.getString(i));
                                    }
                                } while (rs.next());

                                document.add(table);
                            } else {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "No se encontraron datos para el rango de fechas seleccionado.");
                                alert.showAndWait();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al ejecutar la consulta: " + e.getMessage());
                            alert.showAndWait();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Error al preparar la consulta: " + e.getMessage());
                        alert.showAndWait();
                    }
                }

                document.close();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al generar el archivo PDF: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}