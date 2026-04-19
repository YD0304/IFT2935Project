package com.ift2935.view;

import com.ift2935.model.Utilisateur;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Interface pour l'Expert (pmacgillespie2@weibo.com).
 */
public class expert {
    private final Stage stage;
    private final Utilisateur user;

    public expert(Stage stage, Utilisateur user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(javafx.geometry.Pos.CENTER);

        Label welcome = new Label("Bienvenue, Expert " + user.getNom());
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label info = new Label("Votre rôle est d'estimer les produits soumis par les annonceurs.");
        Label note = new Label("Note: Actuellement, l'expertise est simulée dans l'interface Annonceur lors de la soumission.");
        
        root.getChildren().addAll(welcome, info, note);
        
        stage.setScene(new Scene(root, 500, 400));
        stage.setTitle("Espace Expert - " + user.getNom());
        stage.show();
    }
}
