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
public class EditarTareaController {

    @FXML private TextField nombreField;
    @FXML private TextArea descripcionField;
    @FXML private ComboBox<String> estadoCombo;
    @FXML private Spinner<Integer> prioridadSpinner;
    @FXML private DatePicker fechaLimitePicker;
    
    private final TareaService tareaService;
    private Tarea tareaActual;

    @Autowired
    public EditarTareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }
    
    @FXML
    public void initialize() {
        estadoCombo.getItems().addAll("POR_HACER", "EN_PROGRESO", "COMPLETADA");
        
        prioridadSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3)
        );
        
        cargarDatosTarea();
    }

    private void cargarDatosTarea() {
        tareaActual = Session.getCurrentTarea();
        if (tareaActual != null) {
            nombreField.setText(tareaActual.getNombre());
            descripcionField.setText(tareaActual.getDescripcion());
            estadoCombo.setValue(tareaActual.getEstado());
            prioridadSpinner.getValueFactory().setValue(tareaActual.getPrioridad());
            fechaLimitePicker.setValue(tareaActual.getFechaLimite());
        }
    }

    @FXML
    private void handleGuardar() {
        if (validarCampos()) {
            tareaActual.setNombre(nombreField.getText());
            tareaActual.setDescripcion(descripcionField.getText());
            tareaActual.setEstado(estadoCombo.getValue());
            tareaActual.setPrioridad(prioridadSpinner.getValue());
            tareaActual.setFechaLimite(fechaLimitePicker.getValue());
            
            tareaService.save(tareaActual);
            cerrarVentana();
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }
    
    private boolean validarCampos() {
        if (nombreField.getText().isEmpty()) {
            mostrarError("Error", "El nombre es obligatorio");
            return false;
        }
        if (fechaLimitePicker.getValue() == null) {
            mostrarError("Error", "La fecha límite es obligatoria");
            return false;
        }
        return true;
    }
    
    private void cerrarVentana() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}