package com.example.Scrumvest.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class HistoriaUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;
    private String descripcion;
    private String criteriosAceptacion;
    private String prioridad; // ALTA, MEDIA, BAJA
    private String estado; // POR_HACER, EN_PROGRESO, TERMINADA
    
    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;
    
    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;
    
    @OneToMany(mappedBy = "historiaUsuario", cascade = CascadeType.ALL)
    private List<Tarea> tareas;
    
    // Otros campos que necesites
}