package com.example.bitacoracuentas;

import java.time.LocalDate;

public class Ingreso {
    private String descripcion;
    private String categoria;
    private double monto;
    private LocalDate fecha;

    public Ingreso(String descripcion, String categoria, double monto, LocalDate fecha) {
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
