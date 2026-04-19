package com.ift2935.view;

import com.ift2935.dao.UtilisateurDAO;
import com.ift2935.model.Utilisateur;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {
    private final UtilisateurDAO userDAO = new UtilisateurDAO();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("IFT2935 - Système de Vente");

        VBox root = new VBox(20);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(450);

        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Saisissez votre courriel (LMD)...");
        emailField.setPrefHeight(40);

        Button loginBtn = new Button("Se connecter");
        loginBtn.setPrefHeight(40);
        loginBtn.setPrefWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) return;

            try {
                Utilisateur user = userDAO.findByEmail(email);

                if (user != null) {
                    primaryStage.hide();
                    // ✅ Correction : Nom exact de la méthode dans Utilisateur.java
                    String role = user.getType_utilisateur(); 
                    
                    if ("annonceur".equalsIgnoreCase(role)) {
                        new AnnonceurView(new Stage(), user).show();
                    } else if ("acheteur".equalsIgnoreCase(role)) {
                        new AcheteurView(new Stage(), user).show();
                    } else if ("expert".equalsIgnoreCase(role)) {
                        new expert(new Stage(), user).show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Email introuvable.").show();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erreur DB : " + ex.getMessage()).show();
            }
        });

        root.getChildren().addAll(title, new Label("Identifiant unique (Email) :"), emailField, loginBtn);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
