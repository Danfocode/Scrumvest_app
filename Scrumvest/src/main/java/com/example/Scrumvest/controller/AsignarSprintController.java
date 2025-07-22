package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Account;
import com.example.Scrumvest.model.entity.ProyectoColaborador;
import com.example.Scrumvest.model.entity.Sprint;
import com.example.Scrumvest.service.ProyectoColaboradorService;
import com.example.Scrumvest.util.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AsignarSprintController {

    private Sprint sprint;

    @FXML private TableView<Account> colaboradoresTable;
    @FXML private TableColumn<Account, String> nombreColumn;
    @FXML private Button asignarBtn;

    private final ProyectoColaboradorService proyectoColaboradorService;

    @Autowired
    public AsignarSprintController(ProyectoColaboradorService proyectoColaboradorService) {
        this.proyectoColaboradorService = proyectoColaboradorService;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
        cargarColaboradores();
    }

    @FXML
    public void initialize() {
        nombreColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getNombre() + " " + data.getValue().getApellido()));
    }

    private void cargarColaboradores() {
        Long proyectoId = Session.getCurrentProject().getId();
        List<ProyectoColaborador> lista = proyectoColaboradorService.obtenerColaboradoresDeProyecto(proyectoId);
        List<Account> usuarios = lista.stream()
                .map(ProyectoColaborador::getUsuario)
                .toList();

        colaboradoresTable.setItems(FXCollections.observableArrayList(usuarios));
    }

    @FXML
    private void handleAsignar() {
        Account seleccionado = colaboradoresTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            System.out.println("Asignado " + seleccionado.getNombre() + " al Sprint: " + sprint.getNombre());
            showInfo("Asignación simulada", "Colaborador asignado al sprint.\n(Implementa la lógica real aquí)");
        } else {
            showAlert("Seleccione un colaborador para asignar.");
        }
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) colaboradoresTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
