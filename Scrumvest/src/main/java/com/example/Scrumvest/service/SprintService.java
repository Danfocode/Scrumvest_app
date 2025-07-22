package com.example.Scrumvest.service;

import com.example.Scrumvest.model.entity.Sprint;
import com.example.Scrumvest.model.repo.SprintRepo;
import com.example.Scrumvest.util.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SprintService {

    private final SprintRepo sprintRepo;

    @Autowired
    public SprintService(SprintRepo sprintRepo) {
        this.sprintRepo = sprintRepo;
    }

    // Método nuevo añadido
    public List<Sprint> findByUsuarioIdAndEstado(Long userId, String estado) {
        return sprintRepo.findByUsuarioIdAndEstado(userId, estado);
    }

    public List<Sprint> findByProyecto(Long proyectoId) {
        return sprintRepo.findByProyectoId(proyectoId);
    }

    public List<Sprint> findByProyectoAndEstado(Long proyectoId, String estado) {
        return sprintRepo.findByProyectoIdAndEstado(proyectoId, estado);
    }

    public Sprint save(Sprint sprint) {
        return sprintRepo.save(sprint);
    }

    public void delete(Long id) {
        sprintRepo.deleteById(id);
    }

    public int countActiveByUsuario(Long userId) {
        return sprintRepo.countByUsuarioIdAndEstado(userId, "EN_CURSO");
    }
    
    public Sprint cambiarEstado(Long sprintId, String nuevoEstado) {
        Sprint sprint = sprintRepo.findById(sprintId).orElseThrow();
        sprint.setEstado(nuevoEstado.toUpperCase());
        Sprint updated = sprintRepo.save(sprint);
        Session.notifyDataChange();
        return updated;
    }
}