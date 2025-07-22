package com.example.Scrumvest.service;

import com.example.Scrumvest.model.entity.Tarea;
import com.example.Scrumvest.model.repo.TareaRepo;
import com.example.Scrumvest.util.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TareaService {

    private final TareaRepo tareaRepo;

    @Autowired
    public TareaService(TareaRepo tareaRepo) {
        this.tareaRepo = tareaRepo;
    }

    public List<Tarea> findBySprint(Long sprintId) {
        return tareaRepo.findBySprintId(sprintId);
    }

    public Tarea save(Tarea tarea) {
        return tareaRepo.save(tarea);
    }

    public void delete(Long id) {
        tareaRepo.deleteById(id);
    }

    public int countPendientesBySprint(Long sprintId) {
        return tareaRepo.countPendientesBySprint(sprintId);
    }

    public int countPendientesByUsuario(Long usuarioId) {
        return tareaRepo.countPendientesByUsuario(usuarioId);
    }

    public List<Tarea> findByUsuarioAndEstado(Long usuarioId, String estado) {
        return tareaRepo.findByAsignadoIdAndEstado(usuarioId, estado);
    }
    
    
    
    public Tarea cambiarEstadoTarea(Long tareaId, String nuevoEstado) {
        Tarea tarea = tareaRepo.findById(tareaId).orElseThrow();
        tarea.setEstado(nuevoEstado);
        Tarea updated = tareaRepo.save(tarea);
        Session.notifyTaskChange(); // Notificar el cambio
        return updated;
    }
    
    
}