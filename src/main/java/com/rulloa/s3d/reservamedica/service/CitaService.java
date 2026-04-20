package com.rulloa.s3d.reservamedica.service;
import com.rulloa.s3d.reservamedica.model.Cita;


import java.util.List;

public interface CitaService {
    List<Cita> listarCitas(String fecha, Integer idProfesional);
    Cita programarCita(Cita solicitud);
    Cita cancelarCita(Integer id);
    List<Cita> buscarDisponibilidad(String fecha, Integer idProfesional);

}
