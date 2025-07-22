
package com.example.Scrumvest.repository;



import com.example.Scrumvest.model.entity.HistoriaUsuario;
import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.model.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriaUsuarioRepository extends JpaRepository<HistoriaUsuario, Long> {
    List<HistoriaUsuario> findByProyecto(Proyecto proyecto);
    List<HistoriaUsuario> findByEstado(String estado);
    List<HistoriaUsuario> findByPrioridad(String prioridad);
    List<HistoriaUsuario> findByTituloContainingIgnoreCase(String titulo);
    List<HistoriaUsuario> findBySprint(Sprint sprint);
}

