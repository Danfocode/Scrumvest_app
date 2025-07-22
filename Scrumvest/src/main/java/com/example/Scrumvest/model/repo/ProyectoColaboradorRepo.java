package com.example.Scrumvest.model.repo;

import com.example.Scrumvest.model.entity.ProyectoColaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProyectoColaboradorRepo extends JpaRepository<ProyectoColaborador, Long> {

    List<ProyectoColaborador> findByProyectoId(Long proyectoId);  // <- FALTABA

    List<ProyectoColaborador> findByUsuarioId(Long usuarioId);

    boolean existsByProyectoIdAndUsuarioId(Long proyectoId, Long usuarioId); // <- FALTABA
}
