package com.example.Scrumvest.service;

import com.example.Scrumvest.model.entity.Tarea;
import com.example.Scrumvest.model.repo.TareaRepo;
import com.example.Scrumvest.util.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TareaService {

    private final TareaRepo tareaRepo;
    private Map<Long, PriorityQueue<Tarea>> sprintTaskHeaps; // Min-heap por sprint
    private Map<Long, Map<Long, Tarea>> taskMaps; // Mapa de ID a Tarea por sprint

    @Autowired
    public TareaService(TareaRepo tareaRepo) {
        this.tareaRepo = tareaRepo;
        this.sprintTaskHeaps = new HashMap<>();
        this.taskMaps = new HashMap<>();
    }

    // Cargar tareas de un sprint y construir el montículo
    public List<Tarea> findBySprint(Long sprintId) {
        List<Tarea> tareas = tareaRepo.findBySprintId(sprintId);
        if (!sprintTaskHeaps.containsKey(sprintId)) {
            PriorityQueue<Tarea> heap = new PriorityQueue<>((t1, t2) -> t1.getPrioridad() - t2.getPrioridad());
            Map<Long, Tarea> taskMap = new HashMap<>();
            for (Tarea tarea : tareas) {
                heap.offer(tarea);
                taskMap.put(tarea.getId(), tarea);
            }
            sprintTaskHeaps.put(sprintId, heap);
            taskMaps.put(sprintId, taskMap);
        }
        return getTasksInPriorityOrder(sprintId);
    }

    // Guardar una tarea y actualizar el montículo
    public Tarea save(Tarea tarea) {
        Tarea savedTarea = tareaRepo.save(tarea);
        Long sprintId = tarea.getSprint() != null ? tarea.getSprint().getId() : null;
        if (sprintId != null) {
            PriorityQueue<Tarea> heap = sprintTaskHeaps.computeIfAbsent(sprintId, k -> new PriorityQueue<>((t1, t2) -> t1.getPrioridad() - t2.getPrioridad()));
            Map<Long, Tarea> taskMap = taskMaps.computeIfAbsent(sprintId, k -> new HashMap<>());
            
            // Si la tarea ya existía, removerla del heap
            taskMap.remove(savedTarea.getId());
            heap.removeIf(t -> t.getId().equals(savedTarea.getId()));
            
            // Añadir la tarea actualizada
            heap.offer(savedTarea);
            taskMap.put(savedTarea.getId(), savedTarea);
        }
        Session.notifyTaskChange();
        return savedTarea;
    }

    // Eliminar una tarea y actualizar el montículo
    public void delete(Long id) {
        tareaRepo.findById(id).ifPresent(tarea -> {
            Long sprintId = tarea.getSprint() != null ? tarea.getSprint().getId() : null;
            if (sprintId != null) {
                PriorityQueue<Tarea> heap = sprintTaskHeaps.get(sprintId);
                Map<Long, Tarea> taskMap = taskMaps.get(sprintId);
                if (heap != null && taskMap != null) {
                    heap.removeIf(t -> t.getId().equals(id));
                    taskMap.remove(id);
                }
            }
            tareaRepo.deleteById(id);
            Session.notifyTaskChange();
        });
    }

    // Obtener tareas en orden de prioridad
    public List<Tarea> getTasksInPriorityOrder(Long sprintId) {
        PriorityQueue<Tarea> heap = sprintTaskHeaps.get(sprintId);
        if (heap == null) {
            return new ArrayList<>();
        }
        // Crear una copia para no modificar el heap original
        PriorityQueue<Tarea> tempHeap = new PriorityQueue<>(heap);
        List<Tarea> orderedTasks = new ArrayList<>();
        while (!tempHeap.isEmpty()) {
            orderedTasks.add(tempHeap.poll());
        }
        return orderedTasks;
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
        Tarea updated = save(tarea); // Usar save para actualizar el montículo
        return updated;
    }
}