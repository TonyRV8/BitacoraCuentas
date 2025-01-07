package com.example.bitacoracuentas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConsejosController {

    @FXML
    private TableView<Consejo> consejosTable;

    @FXML
    private TableColumn<Consejo, String> colTitulo;

    @FXML
    private TableColumn<Consejo, String> colContenido;

    private ObservableList<Consejo> consejosList;

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colTitulo.setCellValueFactory(cellData -> cellData.getValue().tituloProperty());
        colContenido.setCellValueFactory(cellData -> cellData.getValue().contenidoProperty());

        colTitulo.setPrefWidth(200); // Ajusta el ancho que desees
        colContenido.setPrefWidth(600);
        colTitulo.setResizable(false);
        colContenido.setResizable(false);

        // Ajuste de línea para la columna de contenido
        colContenido.setCellFactory(tc -> new TableCell<Consejo, String>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(colContenido.widthProperty().subtract(10));
                setGraphic(text);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    text.setText(null);
                } else {
                    text.setText(item);
                }
            }
        });

        // Inicializar lista de consejos
        consejosList = FXCollections.observableArrayList();
        consejosTable.setItems(consejosList);

        // Cargar datos desde la base de datos
        cargarConsejos();
    }

    private void cargarConsejos() {
        String query = "SELECT titulo, contenido FROM consejos_financieros";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            consejosList.clear();

            while (resultSet.next()) {
                String titulo = resultSet.getString("titulo");
                String contenido = resultSet.getString("contenido");

                Consejo consejo = new Consejo(titulo, contenido);
                consejosList.add(consejo);
            }

            System.out.println("Consejos cargados correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al cargar los consejos.");
        }
    }
}
