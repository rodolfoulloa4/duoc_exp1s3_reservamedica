package com.rulloa.s3d.reservamedica.service;

import com.rulloa.s3d.reservamedica.model.Profesional;
import java.util.List;
import java.util.Optional;

public interface ProfesionalService {
    List<Profesional> listarProfesionales();
    Optional<Profesional> obtenerProfesional(Integer id);
    Profesional guardarProfesional(Profesional profesional);
    void eliminarProfesional(Integer id);
}
