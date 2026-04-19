package com.ift2935.view;

import com.ift2935.DBConnection;
import com.ift2935.model.Estimation;
import com.ift2935.model.Utilisateur;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class expert {
    private final Stage stage;
    private final Utilisateur user;
    private TableView<Estimation> table;

    public expert(Stage stage, Utilisateur user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        VBox root = new VBox(20); root.setPadding(new Insets(30));
        Label h = new Label("Espace Expert - Historique Global");
        h.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        table = new TableView<>();
        TableColumn<Estimation, Integer> c1 = new TableColumn<>("Produit #");
        c1.setCellValueFactory(new PropertyValueFactory<>("id_produit"));
        TableColumn<Estimation, String> c2 = new TableColumn<>("Prix Estimé");
        c2.setCellValueFactory(new PropertyValueFactory<>("prix_estime"));
        TableColumn<Estimation, String> c3 = new TableColumn<>("Décision Vendeur");
        c3.setCellValueFactory(new PropertyValueFactory<>("decision_annonceur"));
        
        table.getColumns().addAll(c1, c2, c3);
        table.setPrefHeight(400);

        root.getChildren().addAll(h, new Label("Historique complet des estimations :"), table);
        refresh();
        stage.setScene(new Scene(root, 750, 550));
        stage.show();
    }

    private void refresh() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            // Point 7: Charger l'historique réel de la base
            ResultSet rs = stmt.executeQuery("SELECT * FROM Estimation ORDER BY date_estimation DESC");
            List<Estimation> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Estimation(
                    rs.getInt("id_estimation"), rs.getInt("id_produit"), rs.getInt("id_expert"),
                    rs.getBigDecimal("prix_estime"), rs.getString("commentaire"),
                    rs.getDate("date_estimation"), rs.getString("decision_annonceur"), rs.getDate("date_decision")
                ));
            }
            table.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) { e.printStackTrace(); }
    }
}
