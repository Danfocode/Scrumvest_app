package com.example.Scrumvest.controller;

import com.example.Scrumvest.model.entity.Account;
import com.example.Scrumvest.service.AccountService;
import com.example.Scrumvest.util.Session;
import com.example.Scrumvest.util.SpringContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;

    @FXML private TextField usernameField;
    @FXML private TextField emailRegisterField;
    @FXML private PasswordField passwordRegisterField;
    @FXML private ComboBox<String> roleRegisterCombo;

    private final AccountService accountService;

    @Autowired
    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @FXML
    public void initialize() {
        if (roleCombo != null) {
            roleCombo.getItems().addAll("PO", "SM", "DEV");
            roleCombo.setValue("DEV");
        }

        if (roleRegisterCombo != null) {
            roleRegisterCombo.getItems().addAll("PO", "SM", "DEV");
            roleRegisterCombo.setValue("DEV");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String selectedRole = roleCombo.getValue();

        accountService.login(email, password).ifPresentOrElse(account -> {
            if (account.hasRole(selectedRole)) {
                Session.setCurrentUser(account);
                Session.setCurrentRole(selectedRole);
                navigateToHome();
            } else {
                showAlert(Alert.AlertType.ERROR, "No tienes permiso para acceder como " + selectedRole);
            }
        }, () -> {
            showAlert(Alert.AlertType.ERROR, "Credenciales inválidas");
        });
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Register.fxml"));
            loader.setControllerFactory(SpringContext.getApplicationContext()::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro - Scrumvest");
        } catch (Exception e) {
            showErrorAlert("Error al cargar el formulario de registro", e);
        }
    }
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;

    @FXML
    private void handleRegister(ActionEvent event) {
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String username = usernameField.getText();
        String email = emailRegisterField.getText();
        String password = passwordRegisterField.getText();
        String role = roleRegisterCombo.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Todos los campos son obligatorios");
            return;
        }

        Account newAccount = new Account(nombre, apellido, username, email, password);
        newAccount.addRole(role);

        try {
            accountService.register(newAccount);
            showAlert(Alert.AlertType.INFORMATION, "Registro exitoso");
            goToLogin(event);
        } catch (Exception e) {
            showErrorAlert("Error al registrar la cuenta", e);
        }
    }


    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            loader.setControllerFactory(SpringContext.getApplicationContext()::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) emailRegisterField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio de Sesión - Scrumvest");
        } catch (Exception e) {
            showErrorAlert("Error al cargar el formulario de login", e);
        }
    }

    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            loader.setControllerFactory(SpringContext.getApplicationContext()::getBean);
            Parent root = loader.load();

            configureHomeForRole(root);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio - Scrumvest");
        } catch (Exception e) {
            showErrorAlert("Error al cargar la pantalla principal", e);
        }
    }

    private void configureHomeForRole(Parent root) {
        String role = Session.getCurrentRole();
        // Configuración personalizada por rol si es necesario
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.show();
    }

    private void showErrorAlert(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.show();
        e.printStackTrace();
    }
}
