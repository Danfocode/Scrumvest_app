package com.example.Scrumvest.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String estado; // PLANIFICADO, EN_CURSO, FINALIZADO, etc.
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String objetivos;
    
    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Account usuario;
    
    // Constructor, getters y setters generados por @Data
}