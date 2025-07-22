package com.example.Scrumvest.service;

import com.example.Scrumvest.model.entity.HistoriaUsuario;
import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.model.entity.Sprint;
import com.example.Scrumvest.repository.HistoriaUsuarioRepository;
import com.example.Scrumvest.util.AVLTree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class HistoriaUsuarioService {

    @Autowired
    private HistoriaUsuarioRepository repository;

    private AVLTree avlTree = new AVLTree();
    private Proyecto currentProyecto;

    // Inicializar el árbol AVL con las historias de un proyecto
    public void setProyectoActual(Proyecto proyecto) {
        this.currentProyecto = proyecto;
        avlTree = new AVLTree();
        if (proyecto != null) {
            List<HistoriaUsuario> historias = repository.findByProyecto(proyecto);
            for (HistoriaUsuario historia : historias) {
                avlTree.insert(historia.getId(), historia);
            }
        }
    }

    public List<HistoriaUsuario> listarTodas() {
        List<HistoriaUsuario> result = new ArrayList<>();
        avlTree.inOrder(result);
        return result;
    }

    public List<HistoriaUsuario> listarPorProyecto(Proyecto proyecto) {
        if (!proyecto.equals(currentProyecto)) {
            setProyectoActual(proyecto);
        }
        List<HistoriaUsuario> result = new ArrayList<>();
        avlTree.inOrder(result);
        return result;
    }

    @Transactional
    public HistoriaUsuario guardar(HistoriaUsuario historia) {
        HistoriaUsuario savedHistoria = repository.save(historia);
        avlTree.insert(savedHistoria.getId(), savedHistoria);
        return savedHistoria;
    }

    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
        avlTree.delete(id);
    }

    public HistoriaUsuario buscarPorId(Long id) {
        return avlTree.get(id);
    }

    public List<HistoriaUsuario> filtrarPorEstado(String estado) {
        List<HistoriaUsuario> allHistorias = new ArrayList<>();
        avlTree.inOrder(allHistorias);
        List<HistoriaUsuario> result = new ArrayList<>();
        for (HistoriaUsuario historia : allHistorias) {
            if (historia.getEstado().equals(estado)) {
                result.add(historia);
            }
        }
        return result;
    }

    public List<HistoriaUsuario> filtrarPorPrioridad(String prioridad) {
        List<HistoriaUsuario> allHistorias = new ArrayList<>();
        avlTree.inOrder(allHistorias);
        List<HistoriaUsuario> result = new ArrayList<>();
        for (HistoriaUsuario historia : allHistorias) {
            if (historia.getPrioridad().equals(prioridad)) {
                result.add(historia);
            }
        }
        return result;
    }

    public List<HistoriaUsuario> buscarPorTitulo(String titulo) {
        List<HistoriaUsuario> allHistorias = new ArrayList<>();
        avlTree.inOrder(allHistorias);
        List<HistoriaUsuario> result = new ArrayList<>();
        for (HistoriaUsuario historia : allHistorias) {
            if (historia.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                result.add(historia);
            }
        }
        return result;
    }

    public List<HistoriaUsuario> listarPorSprint(Sprint sprint) {
        List<HistoriaUsuario> allHistorias = new ArrayList<>();
        avlTree.inOrder(allHistorias);
        List<HistoriaUsuario> result = new ArrayList<>();
        for (HistoriaUsuario historia : allHistorias) {
            if (historia.getSprint() != null && historia.getSprint().equals(sprint)) {
                result.add(historia);
            }
        }
        return result;
    }
}