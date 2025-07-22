package com.example.Scrumvest.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Account usuario;

    // Constructor personalizado para solo asignar ID
    public Proyecto(Long id) {
        this.id = id;
    }

    // Constructor por defecto necesario para JPA
    public Proyecto() {}
}
