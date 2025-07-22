package com.example.Scrumvest.model.repo;

import com.example.Scrumvest.model.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface TareaRepo extends JpaRepository<Tarea, Long> {
    List<Tarea> findBySprintId(Long sprintId);
    
    @Query("SELECT COUNT(t) FROM Tarea t WHERE t.sprint.id = :sprintId AND t.estado = 'POR_HACER'")
    int countPendientesBySprint(Long sprintId);
    
    // Corrección importante: agregar join con Sprint y Proyecto
    @Query("SELECT COUNT(t) FROM Tarea t JOIN t.sprint s JOIN s.proyecto p WHERE p.usuario.id = :usuarioId AND t.estado = 'POR_HACER'")
    int countPendientesByUsuario(@Param("usuarioId") Long usuarioId);
    
    List<Tarea> findByAsignadoIdAndEstado(Long usuarioId, String estado);


}