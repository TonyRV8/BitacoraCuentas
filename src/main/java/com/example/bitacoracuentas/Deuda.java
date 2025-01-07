package com.example.bitacoracuentas;

import java.time.LocalDate;

public class Deuda {
    private String acreedor;
    private double monto;
    private double saldoRestante;
    private double tasaInteres;
    private LocalDate fechaPago;
    private String estado; // Nuevo atributo

    // Constructor con estado
    public Deuda(String acreedor, double monto, double saldoRestante, double tasaInteres, LocalDate fechaPago, String estado) {
        this.acreedor = acreedor;
        this.monto = monto;
        this.saldoRestante = saldoRestante;
        this.tasaInteres = tasaInteres;
        this.fechaPago = fechaPago;
        this.estado = estado;
    }

    // Getters y Setters
    public String getAcreedor() {
        return acreedor;
    }

    public void setAcreedor(String acreedor) {
        this.acreedor = acreedor;
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

    public double getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Método toString para depuración
    @Override
    public String toString() {
        return "Deuda{" +
                "acreedor='" + acreedor + '\'' +
                ", monto=" + monto +
                ", saldoRestante=" + saldoRestante +
                ", tasaInteres=" + tasaInteres +
                ", fechaPago=" + fechaPago +
                ", estado='" + estado + '\'' +
                '}';
    }
}
