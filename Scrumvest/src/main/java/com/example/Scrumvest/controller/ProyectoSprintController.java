package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.service.AccountService;
import com.example.Scrumvest.service.ProyectoService;
import com.example.Scrumvest.util.Session;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableRow;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Component
public class ProyectoSprintController {

    @FXML private TableView<Proyecto> proyectosTable;
    @FXML private TableColumn<Proyecto, String> nombreColumn;
    @FXML private TableColumn<Proyecto, String> descripcionColumn;
    @FXML private TableColumn<Proyecto, String> fechaInicioColumn;
    @FXML private TableColumn<Proyecto, String> fechaFinColumn;

    private final ProyectoService proyectoService;

    @Autowired private AccountService accountService;
    @Autowired private ApplicationContext applicationContext;

    @Autowired
    public ProyectoSprintController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @FXML
    public void initialize() {
        // Configurar columnas
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        fechaInicioColumn.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        fechaFinColumn.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));

        // Doble clic sobre proyecto
        proyectosTable.setRowFactory(tv -> {
            TableRow<Proyecto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Proyecto proyecto = row.getItem();
                    Session.setCurrentProject(proyecto);
                    navigateToSprints();
                }
            });
            return row;
        });

        // Cargar proyectos del usuario (creador + colaborador)
        loadProyectos();
    }

    private void loadProyectos() {
        Long userId = Session.getCurrentUser().getId();
        List<Proyecto> proyectos = proyectoService.obtenerProyectosDeUsuario(userId); // <- método debe incluir ambos tipos
        proyectosTable.setItems(FXCollections.observableArrayList(proyectos));
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadProyectos();
    }

    @FXML
    private void handleBackToHome(ActionEvent event) {
        navigateTo("/views/Home.fxml", "Inicio - Scrumvest");
    }

    @FXML
    private void handleNewProject(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/nuevoProyecto.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Nuevo Proyecto");
            stage.showAndWait();

            loadProyectos();
        } catch (IOException e) {
            showErrorAlert("Error al crear proyecto", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteProject(ActionEvent event) {
        Proyecto selected = proyectosTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Está seguro que desea eliminar este proyecto?");
            alert.setContentText("Esta acción eliminará el proyecto: " + selected.getNombre());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        proyectoService.delete(selected.getId());
                        loadProyectos();
                        showAlert(Alert.AlertType.INFORMATION, "Proyecto eliminado exitosamente");
                    } catch (Exception e) {
                        showErrorAlert("Error al eliminar proyecto", e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleccione un proyecto para eliminar");
        }
    }

    @FXML
    private void handleViewSprints(ActionEvent event) {
        Proyecto selected = proyectosTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Session.setCurrentProject(selected);
            navigateToSprints();
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleccione un proyecto primero");
        }
    }

    private void navigateToSprints() {
        navigateTo("/views/sprints.fxml", "Sprints del Proyecto - " + Session.getCurrentProject().getNombre());
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            root.getProperties().put("viewPath", fxmlPath);
            Stage stage = (Stage) proyectosTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) {
            showErrorAlert("Error de navegación", e.getMessage());
        }
    }

    @FXML
    private void handleEditProject(ActionEvent event) {
        if (!accountService.isCurrentUserInRole("PO")) {
            showAlert(Alert.AlertType.WARNING, "Solo los Product Owners pueden editar proyectos");
            return;
        }

        Proyecto selected = proyectosTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Session.setCurrentProject(selected);
            openDialog("/views/editarProyecto.fxml", "Editar Proyecto");
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleccione un proyecto primero");
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
            loadProyectos();
        } catch (IOException e) {
            showErrorAlert("Error al abrir formulario", e.getMessage());
        }
    }

    @FXML
    private void handleVerHistoriasUsuario(ActionEvent event) {
        Proyecto selected = proyectosTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Session.setCurrentProject(selected);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/HistoriaUsuario.fxml"));
                loader.setControllerFactory(applicationContext::getBean);
                Parent root = loader.load();

                HistoriaUsuarioController controller = loader.getController();
                controller.setProyectoActual(selected);

                Stage nuevaVentana = new Stage();
                nuevaVentana.setScene(new Scene(root));
                nuevaVentana.setTitle("Historias de Usuario - " + selected.getNombre());
                nuevaVentana.show();

            } catch (IOException e) {
                showErrorAlert("Error al abrir historias", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Seleccione un proyecto primero");
        }
    }

    @FXML
    private void handleInvitarColaborador(ActionEvent event) {
        if (!accountService.isCurrentUserInRole("PO")) {
            showAlert(Alert.AlertType.WARNING, "Solo los Product Owners pueden invitar colaboradores");
            return;
        }

        Proyecto selected = proyectosTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Seleccione un proyecto primero");
            return;
        }

        try {
            URL fxml = getClass().getResource("/views/invitarColaborador.fxml");
            FXMLLoader loader = new FXMLLoader(fxml);
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            InvitarColaboradorController controller = loader.getController();
            controller.setProyecto(selected);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Invitar colaborador a " + selected.getNombre());
            stage.showAndWait();

            loadProyectos();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error al abrir diálogo de invitación", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
