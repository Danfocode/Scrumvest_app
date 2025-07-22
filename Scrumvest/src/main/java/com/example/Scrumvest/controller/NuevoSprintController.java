package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Sprint;
import com.example.Scrumvest.service.SprintService;
import com.example.Scrumvest.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NuevoSprintController {

    @FXML private TextField nombreField;
    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    
    private final SprintService sprintService;

    @Autowired
    public NuevoSprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }
    
    @FXML
    public void initialize() {
        // Establecer valores por defecto
        fechaInicioPicker.setValue(LocalDate.now());
        fechaFinPicker.setValue(LocalDate.now().plusWeeks(2));
    }

    @FXML
    private void handleGuardar() {
        if (validateFields()) {
            try {
                Sprint sprint = new Sprint();
                sprint.setNombre(nombreField.getText().trim());
                sprint.setFechaInicio(fechaInicioPicker.getValue());
                sprint.setFechaFin(fechaFinPicker.getValue());
                sprint.setProyecto(Session.getCurrentProject());
                sprint.setUsuario(Session.getCurrentUser());
                sprint.setEstado("EN_CURSO");
                
                sprintService.save(sprint);
                closeWindow();
            } catch (Exception e) {
                showAlert("Error", "No se pudo guardar el sprint: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCancelar() {
        closeWindow();
    }
    
    private boolean validateFields() {
        if (nombreField.getText() == null || nombreField.getText().trim().isEmpty()) {
            showAlert("Error", "El nombre del sprint es obligatorio");
            nombreField.requestFocus();
            return false;
        }
        
        if (fechaInicioPicker.getValue() == null) {
            showAlert("Error", "La fecha de inicio es obligatoria");
            fechaInicioPicker.requestFocus();
            return false;
        }
        
        if (fechaFinPicker.getValue() == null) {
            showAlert("Error", "La fecha de fin es obligatoria");
            fechaFinPicker.requestFocus();
            return false;
        }
        
        if (fechaFinPicker.getValue().isBefore(fechaInicioPicker.getValue())) {
            showAlert("Error", "La fecha de fin debe ser posterior a la fecha de inicio");
            fechaFinPicker.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void closeWindow() {
        try {
            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            System.err.println("Error al cerrar la ventana: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}