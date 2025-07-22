package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.service.ProyectoService;
import com.example.Scrumvest.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NuevoProyectoController {

    @FXML private TextField nombreField;
    @FXML private TextArea descripcionField;
    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    
    private final ProyectoService proyectoService;
    
    @Autowired
    public NuevoProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }
    
    @FXML
    public void initialize() {
        fechaInicioPicker.setValue(LocalDate.now());
        fechaFinPicker.setValue(LocalDate.now().plusWeeks(2));
    }
    
    @FXML
    private void handleGuardar() {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(nombreField.getText());
        proyecto.setDescripcion(descripcionField.getText());
        proyecto.setFechaInicio(fechaInicioPicker.getValue());
        proyecto.setFechaFin(fechaFinPicker.getValue());
        proyecto.setUsuario(Session.getCurrentUser());
        
        proyectoService.save(proyecto);
        closeWindow();
    }
    
    @FXML
    private void handleCancelar() {
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }
}