package com.ift2935.view;

import com.ift2935.dao.UtilisateurDAO;
import com.ift2935.model.Utilisateur;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {
    private final UtilisateurDAO userDAO = new UtilisateurDAO();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Connexion - Vente Conditionnelle");

        VBox root = new VBox(20);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(450);

        Label title = new Label("Authentification");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Saisissez votre courriel...");
        emailField.setPrefHeight(40);

        Button loginBtn = new Button("Accéder au système");
        loginBtn.setPrefHeight(40);
        loginBtn.setPrefWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) return;

            try {
                // AppelL réel au DAO pour récupérer l'utilisateur par email
                Utilisateur user = userDAO.findByEmail(email);

                if (user != null) {
                    primaryStage.hide();
                    // Routage selon le type_utilisateur du DDL
                    switch (user.gettype_utilisateur().toLowerCase()) {
                        case "annonceur": new AnnonceurView(new Stage(), user).show(); break;
                        case "acheteur": new AcheteurView(new Stage(), user).show(); break;
                        case "expert": new expert(new Stage(), user).show(); break;
                        default: new Alert(Alert.AlertType.ERROR, "Rôle inconnu.").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Courriel introuvable dans la base de données.").show();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Erreur de connexion : " + ex.getMessage()).show();
            }
        });

        root.getChildren().addAll(title, new Label("Identifiant unique (Email) :"), emailField, loginBtn);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
