package com.example.Scrumvest.service;

import com.example.Scrumvest.model.entity.HistoriaUsuario;
import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.model.entity.Sprint;
import com.example.Scrumvest.repository.HistoriaUsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ⬅️ IMPORTANTE

import java.util.List;

@Service
public class HistoriaUsuarioService {

    @Autowired
    private HistoriaUsuarioRepository repository;

    public List<HistoriaUsuario> listarTodas() {
        return repository.findAll();
    }

    public List<HistoriaUsuario> listarPorProyecto(Proyecto proyecto) {
        return repository.findByProyecto(proyecto);
    }

    @Transactional // ⬅️ ESTE ES EL CAMBIO CLAVE
    public HistoriaUsuario guardar(HistoriaUsuario historia) {
        return repository.save(historia);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public HistoriaUsuario buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<HistoriaUsuario> filtrarPorEstado(String estado) {
        return repository.findByEstado(estado);
    }

    public List<HistoriaUsuario> filtrarPorPrioridad(String prioridad) {
        return repository.findByPrioridad(prioridad);
    }

    public List<HistoriaUsuario> buscarPorTitulo(String titulo) {
        return repository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<HistoriaUsuario> listarPorSprint(Sprint sprint) {
        return repository.findBySprint(sprint);
    }
}
