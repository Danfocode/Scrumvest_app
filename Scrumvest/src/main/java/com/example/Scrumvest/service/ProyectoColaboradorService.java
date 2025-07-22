package com.example.Scrumvest.service;

import com.example.Scrumvest.model.entity.Account;
import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.model.entity.ProyectoColaborador;
import com.example.Scrumvest.model.repo.ProyectoColaboradorRepo;
import com.example.Scrumvest.service.AccountService;
import com.example.Scrumvest.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProyectoColaboradorService {

    @Autowired
    private ProyectoColaboradorRepo proyectoColaboradorRepo;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProyectoService proyectoService; // Necesario para obtener proyecto por ID

    @Transactional
    public ProyectoColaborador agregarColaborador(Long proyectoId, Long usuarioId, String rol) {
        ProyectoColaborador colaboracion = new ProyectoColaborador();

        // Asignar proyecto solo con el ID (usando constructor)
        Proyecto proyecto = new Proyecto(proyectoId);
        colaboracion.setProyecto(proyecto);

        // Buscar el usuario y asignarlo
        Account usuario = accountService.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));
        colaboracion.setUsuario(usuario);

        colaboracion.setRol(rol);

        return proyectoColaboradorRepo.save(colaboracion);
    }

    @Transactional(readOnly = true)
    public List<ProyectoColaborador> obtenerColaboradoresDeProyecto(Long proyectoId) {
        return proyectoColaboradorRepo.findByProyectoId(proyectoId);
    }

    @Transactional(readOnly = true)
    public List<ProyectoColaborador> obtenerProyectosDeColaborador(Long usuarioId) {
        return proyectoColaboradorRepo.findByUsuarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public boolean esColaborador(Long proyectoId, Long usuarioId) {
        return proyectoColaboradorRepo.existsByProyectoIdAndUsuarioId(proyectoId, usuarioId);
    }
}
