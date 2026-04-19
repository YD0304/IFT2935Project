package com.ift2935;

import com.ift2935.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        System.out.println("Lancement de l'application...");
        
        // Démarrage de la vue de connexion
        LoginView loginView = new LoginView();
        try {
            loginView.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
