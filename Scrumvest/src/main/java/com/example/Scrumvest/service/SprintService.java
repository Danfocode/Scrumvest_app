package com.example.Scrumvest.service;

import com.example.Scrumvest.model.entity.Sprint;
import com.example.Scrumvest.model.repo.SprintRepo;
import com.example.Scrumvest.util.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SprintService {

    private final SprintRepo sprintRepo;

    // Clase interna para el nodo de la lista doblemente enlazada
    private static class SprintNode {
        Sprint sprint;
        SprintNode prev;
        SprintNode next;

        SprintNode(Sprint sprint) {
            this.sprint = sprint;
        }
    }

    // Clase interna para la lista doblemente enlazada
    private static class SprintList {
        SprintNode head;
        SprintNode tail;
    }

    private final Map<Long, SprintList> projectSprintLists; // Lista doblemente enlazada por proyecto
    private final Map<Long, Map<Long, SprintNode>> sprintMaps; // Mapa de ID a nodo por proyecto

    @Autowired
    public SprintService(SprintRepo sprintRepo) {
        this.sprintRepo = sprintRepo;
        this.projectSprintLists = new HashMap<>();
        this.sprintMaps = new HashMap<>();
    }

    // Cargar sprints de un proyecto y construir la lista doblemente enlazada
    public List<Sprint> findByProyecto(Long proyectoId) {
        List<Sprint> sprints = sprintRepo.findByProyectoId(proyectoId);
        if (!projectSprintLists.containsKey(proyectoId)) {
            SprintList list = new SprintList();
            Map<Long, SprintNode> sprintMap = new HashMap<>();
            // Ordenar sprints por fechaInicio
            sprints.sort(Comparator.comparing(Sprint::getFechaInicio));
            SprintNode prev = null;
            for (Sprint sprint : sprints) {
                SprintNode node = new SprintNode(sprint);
                sprintMap.put(sprint.getId(), node);
                if (list.head == null) {
                    list.head = node;
                } else {
                    prev.next = node;
                    node.prev = prev;
                }
                prev = node;
                list.tail = node;
            }
            projectSprintLists.put(proyectoId, list);
            sprintMaps.put(proyectoId, sprintMap);
        }
        return getSprintsInOrder(proyectoId);
    }

    // Guardar un sprint y actualizar la lista
    public Sprint save(Sprint sprint) {
        Sprint savedSprint = sprintRepo.save(sprint);
        Long proyectoId = sprint.getProyecto() != null ? sprint.getProyecto().getId() : null;
        if (proyectoId != null) {
            SprintList list = projectSprintLists.computeIfAbsent(proyectoId, k -> new SprintList());
            Map<Long, SprintNode> sprintMap = sprintMaps.computeIfAbsent(proyectoId, k -> new HashMap<>());

            // Si el sprint ya existía, removerlo
            SprintNode oldNode = sprintMap.get(savedSprint.getId());
            if (oldNode != null) {
                removeNode(list, oldNode);
                sprintMap.remove(savedSprint.getId());
            }

            // Insertar el sprint en la posición correcta (por fechaInicio)
            SprintNode newNode = new SprintNode(savedSprint);
            sprintMap.put(savedSprint.getId(), newNode);
            insertNode(list, newNode);
        }
        Session.notifyDataChange();
        return savedSprint;
    }

    // Eliminar un sprint y actualizar la lista
    public void delete(Long id) {
        sprintRepo.findById(id).ifPresent(sprint -> {
            Long proyectoId = sprint.getProyecto() != null ? sprint.getProyecto().getId() : null;
            if (proyectoId != null) {
                SprintList list = projectSprintLists.get(proyectoId);
                Map<Long, SprintNode> sprintMap = sprintMaps.get(proyectoId);
                if (list != null && sprintMap != null) {
                    SprintNode node = sprintMap.get(id);
                    if (node != null) {
                        removeNode(list, node);
                        sprintMap.remove(id);
                    }
                }
            }
            sprintRepo.deleteById(id);
            Session.notifyDataChange();
        });
    }

    // Obtener sprints en orden cronológico
    public List<Sprint> getSprintsInOrder(Long proyectoId) {
        SprintList list = projectSprintLists.get(proyectoId);
        if (list == null || list.head == null) {
            return new ArrayList<>();
        }
        List<Sprint> orderedSprints = new ArrayList<>();
        SprintNode current = list.head;
        while (current != null) {
            orderedSprints.add(current.sprint);
            current = current.next;
        }
        return orderedSprints;
    }

    // Obtener sprint anterior
    public Sprint getPreviousSprint(Long sprintId, Long proyectoId) {
        SprintNode node = sprintMaps.getOrDefault(proyectoId, new HashMap<>()).get(sprintId);
        return node != null && node.prev != null ? node.prev.sprint : null;
    }

    // Obtener sprint siguiente
    public Sprint getNextSprint(Long sprintId, Long proyectoId) {
        SprintNode node = sprintMaps.getOrDefault(proyectoId, new HashMap<>()).get(sprintId);
        return node != null && node.next != null ? node.next.sprint : null;
    }

    // Insertar un nodo en la posición correcta (por fechaInicio)
    private void insertNode(SprintList list, SprintNode newNode) {
        if (list.head == null) {
            list.head = newNode;
            list.tail = newNode;
            return;
        }
        SprintNode current = list.head;
        while (current != null && current.sprint.getFechaInicio().isBefore(newNode.sprint.getFechaInicio())) {
            current = current.next;
        }
        if (current == null) {
            list.tail.next = newNode;
            newNode.prev = list.tail;
            list.tail = newNode;
        } else {
            newNode.next = current;
            newNode.prev = current.prev;
            if (current.prev != null) {
                current.prev.next = newNode;
            } else {
                list.head = newNode;
            }
            current.prev = newNode;
        }
    }

    // Remover un nodo de la lista
    private void removeNode(SprintList list, SprintNode node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            list.head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            list.tail = node.prev;
        }
    }

    public List<Sprint> findByUsuarioIdAndEstado(Long userId, String estado) {
        return sprintRepo.findByUsuarioIdAndEstado(userId, estado);
    }

    public List<Sprint> findByProyectoAndEstado(Long proyectoId, String estado) {
        return sprintRepo.findByProyectoIdAndEstado(proyectoId, estado);
    }

    public int countActiveByUsuario(Long userId) {
        return sprintRepo.countByUsuarioIdAndEstado(userId, "EN_CURSO");
    }

    public Sprint cambiarEstado(Long sprintId, String nuevoEstado) {
        Sprint sprint = sprintRepo.findById(sprintId).orElseThrow();
        sprint.setEstado(nuevoEstado.toUpperCase());
        Sprint updated = save(sprint); // Usar save para actualizar la lista
        return updated;
    }
}