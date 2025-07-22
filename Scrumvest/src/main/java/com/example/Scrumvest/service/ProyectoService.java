package com.example.Scrumvest.service;

import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.model.entity.ProyectoColaborador;
import com.example.Scrumvest.model.repo.ProyectoColaboradorRepo;
import com.example.Scrumvest.model.repo.ProyectoRepo;
import com.example.Scrumvest.util.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ProyectoService {

    private final ProyectoRepo proyectoRepo;
    private final ProyectoColaboradorRepo proyectoColaboradorRepo;

    @Autowired
    public ProyectoService(ProyectoRepo proyectoRepo, ProyectoColaboradorRepo proyectoColaboradorRepo) {
        this.proyectoRepo = proyectoRepo;
        this.proyectoColaboradorRepo = proyectoColaboradorRepo;
    }

    @Transactional(readOnly = true)
    public List<Proyecto> obtenerProyectosDeUsuario(Long usuarioId) {
        List<Proyecto> comoDueño = proyectoRepo.findByUsuarioId(usuarioId);
        comoDueño.forEach(p -> p.getNombre()); // inicializa proxy

    List<Proyecto> comoColaborador = proyectoColaboradorRepo.findByUsuarioId(usuarioId)
        .stream()
        .map(ProyectoColaborador::getProyecto)
        .filter(Objects::nonNull) // evita NPE
        .peek(p -> p.getNombre()) // inicializa proxy
        .collect(Collectors.toList());


        Set<Proyecto> todos = new LinkedHashSet<>();
        todos.addAll(comoDueño);
        todos.addAll(comoColaborador);

        return new ArrayList<>(todos);
    }

    public List<Proyecto> findByUsuario(Long usuarioId) {
        return proyectoRepo.findByUsuarioId(usuarioId);
    }

    public Proyecto save(Proyecto proyecto) {
        return proyectoRepo.save(proyecto);
    }

    public int countByUsuario(Long userId) {
        return proyectoRepo.countByUsuarioId(userId);
    }

    public void delete(Long id) {
        proyectoRepo.deleteById(id);
        Session.notifyDataChange();
    }
}
