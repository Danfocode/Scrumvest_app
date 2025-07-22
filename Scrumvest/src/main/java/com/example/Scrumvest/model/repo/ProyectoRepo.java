// ProyectoRepo.java
package com.example.Scrumvest.model.repo;

import com.example.Scrumvest.model.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProyectoRepo extends JpaRepository<Proyecto, Long> {
    List<Proyecto> findByUsuarioId(Long usuarioId);
    int countByUsuarioId(Long usuarioId);
}