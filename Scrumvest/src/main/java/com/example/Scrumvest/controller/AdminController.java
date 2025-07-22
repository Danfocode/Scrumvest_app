package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Account;
import com.example.Scrumvest.service.AccountService;
import com.example.Scrumvest.util.Session;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javafx.beans.property.SimpleStringProperty;

@Component
public class AdminController {

    @FXML private TableView<Account> usersTable;
    @FXML private TableColumn<Account, String> usernameColumn;
    @FXML private TableColumn<Account, String> emailColumn;
    @FXML private TableColumn<Account, String> rolesColumn;
    
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Button deleteButton;
    @FXML private Button editButton;

    private final AccountService accountService;

    @Autowired
    public AdminController(AccountService accountService) {
        this.accountService = accountService;
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolesColumn.setCellValueFactory(cellData -> {
            Set<String> roles = cellData.getValue().getRoles();
            return new SimpleStringProperty(roles != null ? String.join(", ", roles) : "");
        });
        
        roleCombo.getItems().addAll("PO", "SM", "DEV");
        roleCombo.setValue("DEV");
        
        loadUsers();
        
        deleteButton.setDisable(true);
        editButton.setDisable(true);
        
        usersTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean userSelected = newSelection != null;
                deleteButton.setDisable(!userSelected);
                editButton.setDisable(!userSelected);
                
                if (userSelected) {
                    populateForm(newSelection);
                }
            });
    }
    
    private void loadUsers() {
        List<Account> users = accountService.findAllUsers();
        usersTable.setItems(FXCollections.observableArrayList(users));
    }
    
    private void populateForm(Account account) {
        usernameField.setText(account.getUsername());
        emailField.setText(account.getEmail());
        passwordField.clear();
        
        if (!account.getRoles().isEmpty()) {
            roleCombo.setValue(account.getRoles().iterator().next());
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        Account selected = usersTable.getSelectionModel().getSelectedItem();
        
        if (usernameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Nombre de usuario y email son obligatorios");
            return;
        }
        
        Account account = selected != null ? selected : new Account();
        account.setUsername(usernameField.getText());
        account.setEmail(emailField.getText());
        
        if (!passwordField.getText().isEmpty() || selected == null) {
            account.setPassword(passwordField.getText());
        }
        
        account.getRoles().clear();
        account.addRole(roleCombo.getValue());
        
        try {
            accountService.register(account);
            loadUsers();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Usuario guardado exitosamente");
        } catch (Exception e) {
            showErrorAlert("Error al guardar usuario", e);
        }
    }
@FXML
private void handleEdit(ActionEvent event) {
    // Aquí puedes implementar lógica futura para edición específica (ahora está vacío)
}

    @FXML
    private void handleDelete(ActionEvent event) {
        Account selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("Eliminar usuario");
            alert.setContentText("¿Está seguro que desea eliminar el usuario " + selected.getUsername() + "?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        accountService.delete(selected.getId());
                        loadUsers();
                        showAlert(Alert.AlertType.INFORMATION, "Usuario eliminado exitosamente");
                    } catch (Exception e) {
                        showErrorAlert("Error al eliminar usuario", e);
                    }
                }
            });
        }
    }

    @FXML
    private void handleNew(ActionEvent event) {
        usersTable.getSelectionModel().clearSelection();
        clearForm();
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        Stage stage = (Stage) usersTable.getScene().getWindow();
        stage.close();
    }
    
    private void clearForm() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        roleCombo.setValue("DEV");
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
    
    private void showErrorAlert(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
}