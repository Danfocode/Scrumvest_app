package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Tarea;
import com.example.Scrumvest.service.TareaService;
import com.example.Scrumvest.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class NuevaTareaController {

    @FXML private TextField nombreField;
    @FXML private TextArea descripcionField;
    @FXML private ComboBox<String> estadoCombo;
    @FXML private Spinner<Integer> prioridadSpinner;
    @FXML private DatePicker fechaLimitePicker;
    
    private final TareaService tareaService;

    @Autowired
    public NuevaTareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }
    
    @FXML
    public void initialize() {
        estadoCombo.getItems().addAll("POR_HACER", "EN_PROGRESO", "COMPLETADA");
        estadoCombo.setValue("POR_HACER");
        
        prioridadSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3)
        );
        
        fechaLimitePicker.setValue(LocalDate.now().plusDays(7));
    }

    @FXML
    private void handleGuardar() {
        if (validateFields()) {
            Tarea tarea = new Tarea();
            tarea.setNombre(nombreField.getText());
            tarea.setDescripcion(descripcionField.getText());
            tarea.setEstado(estadoCombo.getValue());
            tarea.setPrioridad(prioridadSpinner.getValue());
            tarea.setFechaLimite(fechaLimitePicker.getValue());
            tarea.setSprint(Session.getCurrentSprint());
            tarea.setFechaCreacion(LocalDate.now());
            
            tareaService.save(tarea);
            closeWindow();
        }
    }

    @FXML
    private void handleCancelar() {
        closeWindow();
    }
    
    private boolean validateFields() {
        if (nombreField.getText().isEmpty()) {
            showAlert("Error", "El nombre es obligatorio");
            return false;
        }
        return true;
    }
    
    private void closeWindow() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}