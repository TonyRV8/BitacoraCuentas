package com.example.bitacoracuentas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // URL de conexión, usuario y contraseña
    private static final String URL = "jdbc:postgresql://localhost:5432/dbcuentas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    // Método para obtener la conexión
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        return connection;
    }
}
