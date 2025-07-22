package com.example.Scrumvest.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProyectoColaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Proyecto al que pertenece esta colaboración.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    /**
     * Usuario que colabora en el proyecto.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Account usuario;

    /**
     * Rol del colaborador (por ejemplo: PO, SM, DEV, etc.).
     */
    @Column(nullable = false)
    private String rol;
}
