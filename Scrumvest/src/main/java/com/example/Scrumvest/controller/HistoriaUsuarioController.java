package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.HistoriaUsuario;
import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.service.HistoriaUsuarioService;
import com.example.Scrumvest.util.Session;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

@Component
public class HistoriaUsuarioController {

    // Etiquetas y avatar
    @FXML private Label welcomeLabel, userNameLabel, userEmailLabel, projectCountLabel, sprintCountLabel, taskCountLabel;
    @FXML private ImageView userAvatar;

    // Tabla y columnas
    @FXML private TableView<HistoriaUsuario> tablaHistorias;
    @FXML private TableColumn<HistoriaUsuario, String> tituloColumn;
    @FXML private TableColumn<HistoriaUsuario, String> descripcionColumn;
    @FXML private TableColumn<HistoriaUsuario, String> criteriosColumn;
    @FXML private TableColumn<HistoriaUsuario, String> prioridadColumn;
    @FXML private TableColumn<HistoriaUsuario, String> estadoColumn;

    // Campos de formulario
    @FXML private TextField txtTitulo, txtDescripcion, txtCriterios;
    @FXML private ComboBox<String> comboPrioridad, comboEstado;
    @FXML private Button btnGuardar, btnEliminar;

    private Proyecto proyectoActual;

    @Autowired
    private HistoriaUsuarioService historiaService;

    @FXML
    public void initialize() {
        comboPrioridad.setItems(FXCollections.observableArrayList("ALTA", "MEDIA", "BAJA"));
        comboEstado.setItems(FXCollections.observableArrayList("POR_HACER", "EN_PROGRESO", "TERMINADA"));

        tituloColumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        criteriosColumn.setCellValueFactory(new PropertyValueFactory<>("criteriosAceptacion"));
        prioridadColumn.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

        Session.dataChangeProperty().addListener((obs, oldVal, newVal) -> cargarHistorias());
    }

    public void setProyectoActual(Proyecto proyecto) {
        this.proyectoActual = proyecto;
        Platform.runLater(this::cargarHistorias);
    }

    private void cargarHistorias() {
        if (proyectoActual != null) {
            List<HistoriaUsuario> historias = historiaService.listarPorProyecto(proyectoActual);
            tablaHistorias.setItems(FXCollections.observableArrayList(historias));
        } else {
            tablaHistorias.getItems().clear();
        }
    }

    @FXML
    private void guardarHistoria() {
        if (!"PO".equals(Session.getCurrentRole())) {
            mostrarAlerta("Solo el Product Owner puede guardar historias.");
            return;
        }

        if (proyectoActual == null) {
            mostrarAlerta("No hay un proyecto seleccionado.");
            return;
        }

        if (camposInvalidos()) {
            mostrarAlerta("Por favor completa todos los campos.");
            return;
        }

        HistoriaUsuario historia = new HistoriaUsuario();
        historia.setTitulo(txtTitulo.getText());
        historia.setDescripcion(txtDescripcion.getText());
        historia.setCriteriosAceptacion(txtCriterios.getText());
        historia.setPrioridad(comboPrioridad.getValue());
        historia.setEstado(comboEstado.getValue());
        historia.setProyecto(proyectoActual);

        historiaService.guardar(historia);  // <- esto debe estar en un método @Transactional
        limpiarFormulario();
        cargarHistorias();
        Session.notifyDataChange(); // ✅ Este sí existe

    }

    private boolean camposInvalidos() {
        return txtTitulo.getText().isEmpty() || txtDescripcion.getText().isEmpty() ||
               txtCriterios.getText().isEmpty() || comboPrioridad.getValue() == null ||
               comboEstado.getValue() == null;
    }

    @FXML
    private void eliminarHistoria() {
        HistoriaUsuario seleccionada = tablaHistorias.getSelectionModel().getSelectedItem();
        if (seleccionada != null && "PO".equals(Session.getCurrentRole())) {
            historiaService.eliminar(seleccionada.getId());
            cargarHistorias();
        } else {
            mostrarAlerta("Selecciona una historia y asegúrate de ser Product Owner.");
        }
    }



    
    private void limpiarFormulario() {
        txtTitulo.clear();
        txtDescripcion.clear();
        txtCriterios.clear();
        comboPrioridad.getSelectionModel().clearSelection();
        comboEstado.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
