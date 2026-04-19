package com.ift2935.view;

import com.ift2935.model.Estimation;
import com.ift2935.model.Utilisateur;
import com.ift2935.dao.EstimationDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.sql.SQLException;

public class expert {
    private final Stage stage;
    private final Utilisateur user;
    private final EstimationDAO eDAO = new EstimationDAO();

    public expert(Stage stage, Utilisateur user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));

        Label welcome = new Label("Bienvenue, Expert " + user.getNom());
        welcome.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Point 7: Affichage de l'historique des estimations
        Label histLabel = new Label("Mon historique d'estimations :");
        TableView<Estimation> table = new TableView<>();
        
        TableColumn<Estimation, Integer> colP = new TableColumn<>("Produit ID");
        colP.setCellValueFactory(new PropertyValueFactory<>("id_produit"));
        
        TableColumn<Estimation, BigDecimal> colV = new TableColumn<>("Valeur (€)");
        colV.setCellValueFactory(new PropertyValueFactory<>("prix_estime"));
        
        TableColumn<Estimation, String> colD = new TableColumn<>("Décision Vendeur");
        colD.setCellValueFactory(new PropertyValueFactory<>("decision_annonceur"));

        table.getColumns().addAll(colP, colV, colD);

        try {
            // Placeholder: Ici on filtrerait par id_expert si le DAO le permettait.
            // On affiche le contenu global pour la démo.
            table.setItems(FXCollections.observableArrayList()); 
        } catch (Exception e) {}

        root.getChildren().addAll(welcome, histLabel, table);
        stage.setScene(new Scene(root, 700, 500));
        stage.setTitle("Tableau de bord Expert");
        stage.show();
    }
}
