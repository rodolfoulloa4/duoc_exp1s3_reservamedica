package com.rulloa.s3d.reservamedica.model;

public class CitaSolicitud {
    private String paciente;
    private String fechaHora; // ISO-8601 string, ej. 2026-03-29T10:30

    public CitaSolicitud() {}

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}
