package com.ift2935.view;

/*
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginView extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Connexion");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));

        TextField emailField = new TextField();
        PasswordField passField = new PasswordField();
        Button loginBtn = new Button("Se connecter");

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(passField, 1, 1);
        grid.add(loginBtn, 1, 2);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String pwd = passField.getText();
            Utilisateur user = Database.login(email, pwd);
            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Email ou mot de passe incorrect");
                return;
            }
            primaryStage.hide();  // hide login window
            if (user instanceof Acheteur) {
               // Inside LoginView.start() or after successful login
            Stage buyerStage = new Stage();
            Acheteur acheteur = (Acheteur) user;  // after casting
            AcheteurView view = new AcheteurView(buyerStage, acheteur);
            view.show();
            } else if (user instanceof Annonceur) {
                // Stage sellerStage = new Stage();
                // new AnnonceurView(sellerStage, (Annonceur) user).show();
            } else if (user instanceof Expert) {
                // similarly
            }
        });

        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg);
        alert.setTitle(title);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/