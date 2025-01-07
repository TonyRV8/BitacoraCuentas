package com.example.bitacoracuentas;

import java.sql.Date;

public class Inversion {
    private String descripcion;
    private double monto;
    private Double rendimientoEstimado;
    private Date fechaInicio;
    private Date fechaFin;

    public Inversion(String descripcion, double monto, Double rendimientoEstimado, Date fechaInicio, Date fechaFin) {
        this.descripcion = descripcion;
        this.monto = monto;
        this.rendimientoEstimado = rendimientoEstimado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y setters
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

    public Double getRendimientoEstimado() {
        return rendimientoEstimado;
    }

    public void setRendimientoEstimado(Double rendimientoEstimado) {
        this.rendimientoEstimado = rendimientoEstimado;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
}

