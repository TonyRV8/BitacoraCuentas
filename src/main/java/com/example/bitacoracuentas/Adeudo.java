package com.example.bitacoracuentas;

import java.time.LocalDate;

public class Adeudo {
    private String descripcion;
    private double monto;
    private double saldoRestante;
    private String categoria;
    private LocalDate fechaVencimiento;
    private String estado;

    public Adeudo(String descripcion, double monto, double saldoRestante, String categoria, LocalDate fechaVencimiento, String estado) {
        this.descripcion = descripcion;
        this.monto = monto;
        this.saldoRestante = saldoRestante;
        this.categoria = categoria;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    // Getters y Setters
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getSaldoRestante() {
        return saldoRestante;
    }

    public void setSaldoRestante(double saldoRestante) {
        this.saldoRestante = saldoRestante;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Método toString para facilitar la depuración
    @Override
    public String toString() {
        return "Adeudo{" +
                "descripcion='" + descripcion + '\'' +
                ", monto=" + monto +
                ", saldoRestante=" + saldoRestante +
                ", categoria='" + categoria + '\'' +
                ", fechaVencimiento=" + fechaVencimiento +
                ", estado='" + estado + '\'' +
                '}';
    }
}
