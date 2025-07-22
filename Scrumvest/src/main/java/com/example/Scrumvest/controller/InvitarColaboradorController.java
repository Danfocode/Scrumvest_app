package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Account;
import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.service.AccountService;
import com.example.Scrumvest.service.ProyectoColaboradorService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvitarColaboradorController {

    @FXML private TableView<Account> usuariosTable;
    @FXML private TableColumn<Account, String> usernameColumn;
    @FXML private TableColumn<Account, String> emailColumn;
    @FXML private ComboBox<String> rolCombo;

    private Proyecto proyecto;

    private final AccountService accountService;
    private final ProyectoColaboradorService proyectoColaboradorService;

    @Autowired
    public InvitarColaboradorController(AccountService accountService,
                                        ProyectoColaboradorService proyectoColaboradorService) {
        this.accountService = accountService;
        this.proyectoColaboradorService = proyectoColaboradorService;
    }

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Cargar roles posibles
        rolCombo.getItems().addAll("SM", "DEV");
        rolCombo.setValue("DEV");
    }

    /**
     * Este método debe llamarse desde el controlador que abre la vista,
     * una vez se tenga el proyecto actual disponible.
     */
    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
        loadUsuariosDisponibles();
    }

    private void loadUsuariosDisponibles() {
        List<Account> usuarios = accountService.findAllUsers().stream()
            .filter(u -> !u.getId().equals(proyecto.getUsuario().getId())) // Excluir al dueño del proyecto
            .filter(u -> !proyectoColaboradorService.esColaborador(proyecto.getId(), u.getId())) // Excluir si ya es colaborador
            .collect(Collectors.toList());

        usuariosTable.setItems(FXCollections.observableArrayList(usuarios));
    }

    @FXML
    private void handleInvitar() {
        Account seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            proyectoColaboradorService.agregarColaborador(
                proyecto.getId(),
                seleccionado.getId(),
                rolCombo.getValue()
            );
            showAlert("Éxito", "Usuario invitado correctamente como colaborador.");
            loadUsuariosDisponibles(); // Refrescar la lista
        } else {
            showAlert("Error", "Debe seleccionar un usuario para invitar.");
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) usuariosTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
