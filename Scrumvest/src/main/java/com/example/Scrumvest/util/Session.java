package com.example.Scrumvest.util;

import com.example.Scrumvest.model.entity.Account;
import com.example.Scrumvest.model.entity.Proyecto;
import com.example.Scrumvest.model.entity.Sprint;
import com.example.Scrumvest.model.entity.Tarea;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Session {
    private static Account currentUser;
    private static String currentRole;
    private static Proyecto currentProject;
    private static Sprint currentSprint;
    private static Tarea currentTarea;

    // Propiedades observables
    private static final SimpleObjectProperty<Object> dataChange = new SimpleObjectProperty<>();
    private static final SimpleStringProperty currentRoleProperty = new SimpleStringProperty();

    
    private static final SimpleObjectProperty<Object> taskChange = new SimpleObjectProperty<>();

    public static void notifyTaskChange() {
        Platform.runLater(() -> taskChange.set(new Object()));
    }

    public static SimpleObjectProperty<Object> taskChangeProperty() {
        return taskChange;
    }
    
    
    
    
    public static Account getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Account user) {
        currentUser = user;
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static void setCurrentRole(String role) {
        currentRole = role;
        currentRoleProperty.set(role);
    }

    public static SimpleStringProperty currentRoleProperty() {
        return currentRoleProperty;
    }

    public static Proyecto getCurrentProject() {
        return currentProject;
    }

    public static void setCurrentProject(Proyecto project) {
        currentProject = project;
    }

    public static Sprint getCurrentSprint() {
        return currentSprint;
    }

    public static void setCurrentSprint(Sprint sprint) {
        currentSprint = sprint;
    }

    public static Tarea getCurrentTarea() {
        return currentTarea;
    }

    public static void setCurrentTarea(Tarea tarea) {
        currentTarea = tarea;
    }

    public static void clear() {
        currentUser = null;
        currentRole = null;
        currentProject = null;
        currentSprint = null;
        currentTarea = null;
        notifyDataChange();
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean hasProject() {
        return currentProject != null;
    }

    public static boolean hasSprint() {
        return currentSprint != null;
    }

    public static boolean hasTarea() {
        return currentTarea != null;
    }

    public static void notifyDataChange() {
        Platform.runLater(() -> dataChange.set(new Object()));
    }
    
    public static SimpleObjectProperty<Object> dataChangeProperty() {
        return dataChange;
    }
}