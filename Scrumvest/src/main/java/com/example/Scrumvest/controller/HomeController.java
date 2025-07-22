package com.example.Scrumvest.controller;

import com.example.Scrumvest.util.FXMLViewLoader;
import com.example.Scrumvest.model.entity.Account;
import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.model.entity.Sprint;
import com.example.Scrumvest.service.AccountService;
import com.example.Scrumvest.service.ProyectoService;
import com.example.Scrumvest.service.SprintService;
import com.example.Scrumvest.service.TareaService;
import com.example.Scrumvest.util.Session;
import com.example.Scrumvest.util.SpringContext;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import javafx.scene.control.Alert;
import javafx.scene.Node;
import javafx.stage.Modality;
import java.io.IOException;
import javafx.scene.layout.BorderPane;

@Component
public class HomeController {

    @FXML private Label welcomeLabel;
    @FXML private Label userNameLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label projectCountLabel;
    @FXML private Label sprintCountLabel;
    @FXML private Label taskCountLabel;
    @FXML private ImageView userAvatar;
    
    @Autowired private ProyectoService proyectoService;
    @Autowired private SprintService sprintService;
    @Autowired private TareaService tareaService;
    
    @Autowired
    private ApplicationContext applicationContext;

    private final Stack<String> navigationHistory = new Stack<>();

@FXML
public void initialize() {
    loadUserData();
    refreshAllStats();
    
    // Usa solo un listener unificado
    Session.dataChangeProperty().addListener((obs, oldVal, newVal) -> {
        System.out.println("Refrescando todos los stats...");
        refreshAllStats();
    });
}
@Autowired
private FXMLViewLoader fxmlViewLoader;

private void refreshAllStats() {
    Platform.runLater(() -> {
        try {
            Long userId = Session.getCurrentUser().getId();
            
            System.out.println("[DEBUG] Refrescando estadísticas para usuario: " + userId);
            
            // Proyectos
            int proyectos = proyectoService.countByUsuario(userId);
            System.out.println("[DEBUG] Proyectos encontrados: " + proyectos);
            
            // Sprints - Doble verificación
            List<Sprint> sprintsActivos = sprintService.findByUsuarioIdAndEstado(userId, "EN_CURSO");
            System.out.println("[DEBUG] Lista de sprints activos: " + sprintsActivos);
            
            int sprints = sprintsActivos.size(); // Usar el tamaño de la lista directamente
            System.out.println("[DEBUG] Conteo de sprints: " + sprints);
            
            // Tareas
            int tareas = tareaService.countPendientesByUsuario(userId);
            System.out.println("[DEBUG] Tareas encontradas: " + tareas);

            // Actualizar UI
            projectCountLabel.setText(String.valueOf(proyectos));
            sprintCountLabel.setText(String.valueOf(sprints));
            taskCountLabel.setText(String.valueOf(tareas));

        } catch (Exception e) {
            System.err.println("Error al actualizar stats: " + e.getMessage());
            e.printStackTrace();
            // Mostrar valores por defecto en caso de error
            projectCountLabel.setText("0");
            sprintCountLabel.setText("0");
            taskCountLabel.setText("0");
        }
    });
}


    private void refreshSprintCount() {
        Platform.runLater(() -> {
            try {
                Long userId = Session.getCurrentUser().getId();
                int count = sprintService.countActiveByUsuario(userId);
                sprintCountLabel.setText(String.valueOf(count));
                System.out.println("[DEBUG] Sprints actualizados: " + count);
            } catch (Exception e) {
                System.err.println("Error actualizando sprints: " + e.getMessage());
            }
        });
    }

    private void refreshTaskCount() {
        Platform.runLater(() -> {
            try {
                Long userId = Session.getCurrentUser().getId();
                int count = tareaService.countPendientesByUsuario(userId);
                taskCountLabel.setText(String.valueOf(count));
                System.out.println("[DEBUG] Tareas actualizadas: " + count);
            } catch (Exception e) {
                System.err.println("Error actualizando tareas: " + e.getMessage());
            }
        });
    }

    private void loadUserData() {
        try {
            Account currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                welcomeLabel.setText("Bienvenido, " + currentUser.getUsername());
                userNameLabel.setText(currentUser.getUsername());
                userEmailLabel.setText(currentUser.getEmail());
                
                if (userAvatar != null) {
                    Image avatar = new Image(getClass().getResourceAsStream("/images/default-avatar.png"));
                    userAvatar.setImage(avatar);
                }
            } else {
                System.err.println("Usuario no autenticado en sesión");
            }
        } catch (Exception e) {
            System.err.println("Error cargando datos de usuario: " + e.getMessage());
        }
    }

    private void loadUserStats() {
        Long userId = Session.getCurrentUser().getId();
        int proyectosCount = proyectoService.countByUsuario(userId);
        int sprintsCount = sprintService.countActiveByUsuario(userId);
        int tareasCount = tareaService.countPendientesByUsuario(userId);

        projectCountLabel.setText(String.valueOf(proyectosCount));
        sprintCountLabel.setText(String.valueOf(sprintsCount));
        taskCountLabel.setText(String.valueOf(tareasCount));
    }

    @FXML
    private void handleViewProjects(ActionEvent event) {
        // Limpiar historial cuando vamos a la vista raíz
        navigationHistory.clear();
        navigateTo("/views/proyectos.fxml", "Mis Proyectos");
    }

    @FXML
    private void handleViewDashboard(ActionEvent event) {
        navigationHistory.clear();
        navigateTo("/views/dashboard.fxml", "Dashboard");
    }

    @FXML
    private void handleBackNavigation(ActionEvent event) {
        if (!navigationHistory.isEmpty()) {
            String previousView = navigationHistory.pop();
            navigateTo(previousView, getTitleForView(previousView));
        } else {
            // Si no hay historial, volver a proyectos
            navigateTo("/views/proyectos.fxml", "Mis Proyectos");
        }
    }

    @FXML
    private void handleNewProject(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/nuevoProyecto.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Nuevo Proyecto");
            stage.showAndWait();
            
            loadUserStats(); // Actualizar estadísticas
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error al crear proyecto", e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.clear();
        navigateTo("/views/login.fxml", "Inicio de Sesión");
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            // Guardar la vista actual en el historial (excepto para ciertas vistas)
            Scene currentScene = welcomeLabel.getScene();
            if (currentScene != null && currentScene.getRoot() != null) {
                String currentView = (String) currentScene.getRoot().getProperties().get("viewPath");
                if (currentView != null && !fxmlPath.equals("/views/proyectos.fxml") && 
                    !fxmlPath.equals("/views/dashboard.fxml") && !fxmlPath.equals("/views/Home.fxml")) {
                    navigationHistory.push(currentView);
                }
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            
            Parent root = loader.load();
            root.getProperties().put("viewPath", fxmlPath); // Identificar la vista para el historial
            
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error de navegación", "No se pudo cargar la vista: " + fxmlPath);
        }
    }

    
     @Autowired
    private AccountService accountService; // Faltaba esta inyección   
    
    
    
// ... (el código anterior permanece igual hasta los métodos)
@FXML
private void handleAdminPanel(ActionEvent event) {
    try {
        if (!accountService.isCurrentUserInRole("PO")) {
            showAlert(Alert.AlertType.WARNING, 
                    "Acceso restringido", 
                    "Solo los Product Owners pueden acceder a esta función");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Administración de Usuarios");
        
        // Configuración opcional para hacer la ventana modal
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        
        stage.show();
    } catch (IOException e) {
        showErrorAlert("Error de carga", 
                      "No se pudo cargar el panel de administración: " + e.getMessage());
    } catch (Exception e) {
        showErrorAlert("Error inesperado", 
                      "Ocurrió un problema al abrir el panel: " + e.getMessage());
    }
} 
    
    
private void showAlert(Alert.AlertType type, String title, String message) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

private void showAlert(Alert.AlertType type, String message) {
    showAlert(type, null, message);
}

private void showErrorAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}    
    
    
 
    
    
    
    
    
    private String getTitleForView(String fxmlPath) {
        switch (fxmlPath) {
            case "/views/proyectos.fxml": return "Mis Proyectos";
            case "/views/sprints.fxml": return "Sprints del Proyecto";
            case "/views/tareas.fxml": return "Tareas del Sprint";
            case "/views/dashboard.fxml": return "Dashboard";
            case "/views/Home.fxml": return "Inicio";
            default: return "Scrumvest";
        }
    }

}