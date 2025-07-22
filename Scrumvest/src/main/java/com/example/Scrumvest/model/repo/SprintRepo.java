package com.example.Scrumvest.model.repo;

import com.example.Scrumvest.model.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.data.repository.query.Param;


public interface SprintRepo extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProyectoId(Long proyectoId);
    

    
    @Query("SELECT COUNT(s) FROM Sprint s JOIN s.proyecto p WHERE p.usuario.id = :userId AND s.estado = :estado")
    int countByUsuarioIdAndEstado(@Param("userId") Long userId, @Param("estado") String estado);
    
    @Query("SELECT s FROM Sprint s WHERE s.proyecto.id = :proyectoId AND s.estado = :estado")
    List<Sprint> findByProyectoIdAndEstado(@Param("proyectoId") Long proyectoId, @Param("estado") String estado);

    @Query("SELECT s FROM Sprint s WHERE s.usuario.id = :userId AND s.estado = :estado")
    List<Sprint> findByUsuarioIdAndEstado(@Param("userId") Long userId, @Param("estado") String estado);
}