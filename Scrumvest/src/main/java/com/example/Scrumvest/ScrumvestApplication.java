package com.example.Scrumvest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ScrumvestApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        springContext = new SpringApplicationBuilder(ScrumvestApplication.class)
            .headless(false)
            .run();
    }

@Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();
        
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Scrumvest - Login");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
    }
}