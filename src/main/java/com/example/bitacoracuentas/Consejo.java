package com.example.bitacoracuentas;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Consejo {
    private final StringProperty titulo;
    private final StringProperty contenido;

    public Consejo(String titulo, String contenido) {
        this.titulo = new SimpleStringProperty(titulo);
        this.contenido = new SimpleStringProperty(contenido);
    }

    public String getTitulo() {
        return titulo.get();
    }

    public void setTitulo(String titulo) {
        this.titulo.set(titulo);
    }

    public StringProperty tituloProperty() {
        return titulo;
    }

    public String getContenido() {
        return contenido.get();
    }

    public void setContenido(String contenido) {
        this.contenido.set(contenido);
    }

    public StringProperty contenidoProperty() {
        return contenido;
    }
}
