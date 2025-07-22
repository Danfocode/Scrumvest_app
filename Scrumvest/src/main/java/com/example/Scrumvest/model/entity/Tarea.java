package com.example.Scrumvest.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String descripcion;
    private String estado; // POR_HACER, EN_PROGRESO, COMPLETADA
    private Integer prioridad;
    private LocalDate fechaCreacion;
    private LocalDate fechaLimite;
    
    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Account asignado;
    
    // Relación con Historia de Usuario (opcional)
    @ManyToOne
    @JoinColumn(name = "historia_usuario_id")
    private HistoriaUsuario historiaUsuario;
}