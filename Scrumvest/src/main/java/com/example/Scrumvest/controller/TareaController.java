package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Tarea;
import com.example.Scrumvest.service.TareaService;
import com.example.Scrumvest.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Component
public class TareaController {

    @FXML private TableView<Tarea> tareasTable;
    @FXML private TableColumn<Tarea, String> nombreColumn;
    @FXML private TableColumn<Tarea, String> descripcionColumn;
    @FXML private TableColumn<Tarea, String> estadoColumn;
    @FXML private TableColumn<Tarea, Integer> prioridadColumn;
    @FXML private TableColumn<Tarea, LocalDate> fechaLimiteColumn;
    
    private final TareaService tareaService;
    private final ApplicationContext applicationContext;

    @Autowired
    public TareaController(TareaService tareaService, ApplicationContext applicationContext) {
        this.tareaService = tareaService;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        configureTableColumns();
        loadTareas();
    }

    private void configureTableColumns() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        prioridadColumn.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        fechaLimiteColumn.setCellValueFactory(new PropertyValueFactory<>("fechaLimite"));
    }

    private void loadTareas() {
        if (Session.getCurrentSprint() != null) {
            tareasTable.getItems().setAll(
                tareaService.findBySprint(Session.getCurrentSprint().getId())
            );
        }
    }

    @FXML
    private void handleBackToSprints(ActionEvent event) {
        navigateTo("/views/sprints.fxml", "Sprints del Proyecto");
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadTareas();
    }

    @FXML
    private void handleNewTarea(ActionEvent event) {
        openDialog("/views/nuevaTarea.fxml", "Nueva Tarea");
    }

    @FXML
    private void handleEditTarea(ActionEvent event) {
        Tarea selected = tareasTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Session.setCurrentTarea(selected);
            openDialog("/views/editarTarea.fxml", "Editar Tarea");
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleccione una tarea primero");
        }
    }

    @FXML
    private void handleDeleteTarea(ActionEvent event) {
        Tarea selected = tareasTable.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete(selected.getNombre())) {
            tareaService.delete(selected.getId());
            loadTareas();
        }
    }

    // Métodos utilitarios
    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            Stage stage = (Stage) tareasTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (Exception e) {
            showError("Error de navegación", e.getMessage());
        }
    }

    private void openDialog(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.showAndWait();
            loadTareas();
        } catch (Exception e) {
            showError("Error al abrir formulario", e.getMessage());
        }
    }

    
    
    
    
    

    @FXML
    private void handleCompletarTarea() {
        Tarea selected = tareasTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tareaService.cambiarEstadoTarea(selected.getId(), "COMPLETADA");
            loadTareas(); // Refrescar la tabla local
        }
    }

    @FXML
    private void handleReabrirTarea() {
        Tarea selected = tareasTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tareaService.cambiarEstadoTarea(selected.getId(), "PENDIENTE");
            loadTareas();
        }
    }    
    
    
    
    
    
    
    
    
    
    
    
    private boolean confirmDelete(String tareaNombre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("Eliminar Tarea");
        alert.setContentText("¿Está seguro que desea eliminar la tarea '" + tareaNombre + "'?");
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}