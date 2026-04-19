package com.ift2935.view;

import com.ift2935.model.Utilisateur;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;

public class LoginView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Système de Vente Conditionnelle - Connexion");

        VBox root = new VBox(20);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(450);

        Label title = new Label("Authentification");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField emailField = new TextField();
        emailField.setPromptText("Saisissez votre courriel...");
        emailField.setPrefHeight(40);

        Button loginBtn = new Button("Se connecter");
        loginBtn.setPrefHeight(40);
        loginBtn.setPrefWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) return;

            Utilisateur user = performLogin(email);

            if (user != null) {
                primaryStage.hide();
                System.out.println("Role détecté: " + user.getRole()); // Debug
                
                // Redirection selon le type_utilisateur (Annonceur, Acheteur, Expert)
                if ("annonceur".equalsIgnoreCase(user.getRole())) {
                    new AnnonceurView(new Stage(), user).show();
                } else if ("acheteur".equalsIgnoreCase(user.getRole())) {
                    new AcheteurView(new Stage(), user).show();
                } else if ("expert".equalsIgnoreCase(user.getRole())) {
                    new expert(new Stage(), user).show(); 
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Compte inconnu. Utilisez un email du fichier LMD.sql.").show();
            }
        });

        root.getChildren().addAll(title, new Label("Veuillez saisir votre courriel :"), emailField, loginBtn);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private Utilisateur performLogin(String email) {
        // Reconnaissance des comptes du LMD.sql
        if (email.contains("hformoy0")) {
            return new Utilisateur(1, "Formoy", "Hermann", email, "514", "MTL", LocalDate.now(), "annonceur");
        } else if (email.contains("kstephen3")) {
            return new Utilisateur(4, "Stephen", "Konstantin", email, "514", "MTL", LocalDate.now(), "acheteur");
        } else if (email.contains("pmacgillespie2")) {
            return new Utilisateur(3, "MacGillespie", "Patrizius", email, "514", "MTL", LocalDate.now(), "expert");
        }
        
        // Fallback pour tests
        if (email.contains("expert")) return new Utilisateur(999, "Expert", "Demo", email, "0", "Add", LocalDate.now(), "expert");
        if (email.contains("seller")) return new Utilisateur(888, "Vendeur", "Demo", email, "0", "Add", LocalDate.now(), "annonceur");
        if (email.contains("buyer")) return new Utilisateur(777, "Acheteur", "Demo", email, "0", "Add", LocalDate.now(), "acheteur");
        
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
