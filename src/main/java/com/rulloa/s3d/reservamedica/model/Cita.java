package com.rulloa.s3d.reservamedica.model;

import java.time.LocalDateTime;

public class Cita {
    private Long id;
    private String paciente;
    private LocalDateTime inicio;
    private EstadoCita estado;

    public Cita() {}
    public Cita(Long id, String paciente, LocalDateTime inicio, EstadoCita estado) {
        this.id = id;
        this.paciente = paciente;
        this.inicio = inicio;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }
}
