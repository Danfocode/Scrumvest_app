package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Sprint;
import com.example.Scrumvest.service.SprintService;
import com.example.Scrumvest.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import javafx.fxml.FXMLLoader;

@Component
public class SprintController {

    // Componentes FXML
    @FXML private TableView<Sprint> sprintsTable;
    @FXML private TableColumn<Sprint, String> nombreColumn;
    @FXML private TableColumn<Sprint, LocalDate> fechaInicioColumn;
    @FXML private TableColumn<Sprint, LocalDate> fechaFinColumn;
    @FXML private TableColumn<Sprint, String> estadoColumn;
    
    // Dependencias
    private final SprintService sprintService;
    private final ApplicationContext applicationContext;

    @Autowired
    public SprintController(SprintService sprintService, ApplicationContext applicationContext) {
        this.sprintService = sprintService;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        configureTableColumns();
        loadSprints();
        // Suscribirse a cambios en los sprints
        Session.dataChangeProperty().addListener((obs, oldValue, newValue) -> loadSprints());
    }

    private void configureTableColumns() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        fechaInicioColumn.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        fechaFinColumn.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void loadSprints() {
        if (Session.getCurrentProject() != null) {
            sprintsTable.getItems().setAll(
                sprintService.getSprintsInOrder(Session.getCurrentProject().getId())
            );
        }
    }

    @FXML
    private void handleBackToProjects(ActionEvent event) {
        navigateTo("/views/proyectos.fxml", "Mis Proyectos - Scrumvest");
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadSprints();
    }

    @FXML
    private void handleNewSprint(ActionEvent event) {
        openDialog("/views/nuevoSprint.fxml", "Nuevo Sprint");
    }

    @FXML
    private void handleViewSprint(ActionEvent event) {
        Sprint selected = sprintsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Session.setCurrentSprint(selected);
            navigateTo("/views/tareas.fxml", "Tareas del Sprint");
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleccione un sprint primero");
        }
    }

    @FXML
    private void handleDeleteSprint(ActionEvent event) {
        Sprint selected = sprintsTable.getSelectionModel().getSelectedItem();
        if (selected != null && confirmDelete(selected.getNombre())) {
            sprintService.delete(selected.getId());
            loadSprints();
        }
    }

    @FXML
    private void handleIniciarSprint() {
        Sprint selected = sprintsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            sprintService.cambiarEstado(selected.getId(), "EN_CURSO");
            loadSprints();
        }
    }

    @FXML
    private void handleFinalizarSprint() {
        Sprint selected = sprintsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            sprintService.cambiarEstado(selected.getId(), "FINALIZADO");
            loadSprints();
        }
    }

    @FXML
    private void handleAsignarSprint() {
        Sprint selected = sprintsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/asignarSprint.fxml"));
                loader.setControllerFactory(applicationContext::getBean);
                Parent root = loader.load();

                AsignarSprintController controller = loader.getController();
                controller.setSprint(selected);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Asignar colaboradores al sprint");
                stage.showAndWait();

                loadSprints();
            } catch (IOException e) {
                showError("Error al abrir diálogo de asignación", e.getMessage());
            }
        }
    }

    @FXML
    private void handlePreviousSprint() {
        Sprint selected = sprintsTable.getSelectionModel().getSelectedItem();
        if (selected != null && Session.getCurrentProject() != null) {
            Sprint previous = sprintService.getPreviousSprint(selected.getId(), Session.getCurrentProject().getId());
            if (previous != null) {
                sprintsTable.getSelectionModel().select(previous);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No hay sprint anterior.");
            }
        }
    }

    @FXML
    private void handleNextSprint() {
        Sprint selected = sprintsTable.getSelectionModel().getSelectedItem();
        if (selected != null && Session.getCurrentProject() != null) {
            Sprint next = sprintService.getNextSprint(selected.getId(), Session.getCurrentProject().getId());
            if (next != null) {
                sprintsTable.getSelectionModel().select(next);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No hay sprint siguiente.");
            }
        }
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            Stage stage = (Stage) sprintsTable.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
        } catch (IOException e) {
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
            loadSprints();
        } catch (IOException e) {
            showError("Error al abrir formulario", e.getMessage());
        }
    }

    private boolean confirmDelete(String sprintName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("Eliminar Sprint");
        alert.setContentText("¿Está seguro que desea eliminar el sprint '" + sprintName + "'?");
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