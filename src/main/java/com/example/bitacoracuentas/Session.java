package com.example.bitacoracuentas;

public class Session {
    private static int usuarioId; // ID del usuario actual
    private static String nombreUsuario; // Nombre del usuario (opcional)
    private static String correoUsuario; // Correo del usuario (opcional)

    // Métodos para configurar los datos del usuario
    public static void iniciarSesion(int id, String nombre, String correo) {
        usuarioId = id;
        nombreUsuario = nombre;
        correoUsuario = correo;
    }

    // Métodos para obtener los datos del usuario
    public static int getUsuarioId() {
        return usuarioId;
    }

    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    public static String getCorreoUsuario() {
        return correoUsuario;
    }

    // Método para cerrar sesión
    public static void cerrarSesion() {
        usuarioId = 0;
        nombreUsuario = null;
        correoUsuario = null;
    }
}
