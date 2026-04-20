package com.rulloa.s3d.reservamedica.repository;
import com.rulloa.s3d.reservamedica.model.Cita;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

    @Query("SELECT c FROM Cita c WHERE c.profesional.id = :profId AND c.inicio >= :inicio AND c.inicio < :fin AND c.estado = com.rulloa.s3d.reservamedica.model.Cita.EstadoCita.PROGRAMADA")
    List<Cita> findByProfesionalAndFecha(@Param("profId") Integer profId, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT c FROM Cita c WHERE c.profesional.id = :profId AND :inicio < c.fin AND c.inicio < :fin AND c.estado = com.rulloa.s3d.reservamedica.model.Cita.EstadoCita.PROGRAMADA")
    List<Cita> findOverlaps(@Param("profId") Integer profId, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

}
