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

import java.util.regex.Pattern;

@Component
public class AuthController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;

    @FXML private TextField usernameField;
    @FXML private TextField emailRegisterField;
    @FXML private PasswordField passwordRegisterField;
    @FXML private ComboBox<String> roleRegisterCombo;

    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;

    private final AccountService accountService;

    // Patrones para validaciones con expresiones regulares
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[A-Za-z\\s]+$"
    );
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[A-Za-z0-9_]{3,30}$"
    );
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^.{8,}$"
    );

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

        // Validar campos de login
        if (!validarCamposLogin(email, password, selectedRole)) {
            return;
        }

        // Verificar si ya existe una sesión activa en el caché
        if (Session.hasActiveSession(email)) {
            Session.SessionData sessionData = Session.getSession(email);
            if (sessionData.getRole().equals(selectedRole)) {
                Session.setCurrentUser(sessionData.getAccount());
                Session.setCurrentRole(selectedRole);
                navigateToHome();
                return;
            } else {
                showAlert(Alert.AlertType.ERROR, "Sesión activa con un rol diferente. Cierra la sesión actual.");
                return;
            }
        }

        // Proceder con la autenticación si no hay sesión activa
        accountService.login(email, password).ifPresentOrElse(account -> {
            if (account.hasRole(selectedRole)) {
                Session.addSession(email, account, selectedRole); // Añadir al caché de sesiones
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

    @FXML
    private void handleRegister(ActionEvent event) {
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String username = usernameField.getText();
        String email = emailRegisterField.getText();
        String password = passwordRegisterField.getText();
        String role = roleRegisterCombo.getValue();

        // Validar campos de registro
        if (!validarCamposRegistro(nombre, apellido, username, email, password, role)) {
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

    private boolean validarCamposLogin(String email, String password, String role) {
        // Validar correo
        if (email == null || email.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "El correo es obligatorio.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert(Alert.AlertType.WARNING, "El correo debe tener un formato válido (ejemplo: usuario@dominio.com).");
            return false;
        }
        if (email.length() > 100) {
            showAlert(Alert.AlertType.WARNING, "El correo no puede exceder los 100 caracteres.");
            return false;
        }

        // Validar contraseña
        if (password == null || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "La contraseña es obligatoria.");
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showAlert(Alert.AlertType.WARNING, 
                "La contraseña debe tener al menos 8 caracteres");
            return false;
        }

        // Validar rol
        if (role == null) {
            showAlert(Alert.AlertType.WARNING, "Debes seleccionar un rol.");
            return false;
        }

        return true;
    }

    private boolean validarCamposRegistro(String nombre, String apellido, String username, String email, String password, String role) {
        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "El nombre es obligatorio.");
            return false;
        }
        if (!NAME_PATTERN.matcher(nombre).matches()) {
            showAlert(Alert.AlertType.WARNING, "El nombre solo puede contener letras y espacios.");
            return false;
        }
        if (nombre.length() < 2 || nombre.length() > 50) {
            showAlert(Alert.AlertType.WARNING, "El nombre debe tener entre 2 y 50 caracteres.");
            return false;
        }

        // Validar apellido
        if (apellido == null || apellido.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "El apellido es obligatorio.");
            return false;
        }
        if (!NAME_PATTERN.matcher(apellido).matches()) {
            showAlert(Alert.AlertType.WARNING, "El apellido solo puede contener letras y espacios.");
            return false;
        }
        if (apellido.length() < 2 || apellido.length() > 50) {
            showAlert(Alert.AlertType.WARNING, "El apellido debe tener entre 2 y 50 caracteres.");
            return false;
        }

        // Validar nombre de usuario
        if (username == null || username.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "El nombre de usuario es obligatorio.");
            return false;
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            showAlert(Alert.AlertType.WARNING, "El nombre de usuario solo puede contener letras, números y guiones bajos, y debe tener entre 3 y 30 caracteres.");
            return false;
        }

        // Validar correo
        if (email == null || email.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "El correo es obligatorio.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert(Alert.AlertType.WARNING, "El correo debe tener un formato válido (ejemplo: usuario@dominio.com).");
            return false;
        }
        if (email.length() > 100) {
            showAlert(Alert.AlertType.WARNING, "El correo no puede exceder los 100 caracteres.");
            return false;
        }
        if (accountService.existeCorreo(email)) {
            showAlert(Alert.AlertType.WARNING, "El correo ya está registrado.");
            return false;
        }

        // Validar contraseña
        if (password == null || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "La contraseña es obligatoria.");
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showAlert(Alert.AlertType.WARNING, 
                "La contraseña debe tener al menos 8 caracteres, incluyendo una mayúscula, una minúscula, un número y un carácter especial.");
            return false;
        }

        // Validar rol
        if (role == null) {
            showAlert(Alert.AlertType.WARNING, "Debes seleccionar un rol.");
            return false;
        }

        return true;
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