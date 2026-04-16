/*
package com.ift2935.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AcheteurView {
    private Stage stage;
    private Acheteur acheteur;
    private TableView<AdRow> table;

    public AcheteurView(Stage stage, Acheteur acheteur) {
        this.stage = stage;
        this.acheteur = acheteur;
    }

    public void show() {
        stage.setTitle("Espace Acheteur - " + acheteur.getPrenom() + " " + acheteur.getNom());
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        table = new TableView<>();
        TableColumn<AdRow, Integer> colId = new TableColumn<>("ID Annonce");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<AdRow, String> colProd = new TableColumn<>("Produit");
        colProd.setCellValueFactory(new PropertyValueFactory<>("produit"));
        TableColumn<AdRow, String> colVendeur = new TableColumn<>("Vendeur");
        colVendeur.setCellValueFactory(new PropertyValueFactory<>("vendeur"));
        TableColumn<AdRow, Double> colPrix = new TableColumn<>("Prix estimé (caché)");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixEstime"));
        table.getColumns().addAll(colId, colProd, colVendeur, colPrix);

        refreshAds();

        Button offerBtn = new Button("Faire une offre");
        offerBtn.setOnAction(e -> makeOffer());

        root.getChildren().addAll(new Label("Annonces actives :"), table, offerBtn);
        Scene scene = new Scene(root, 700, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void refreshAds() {
        ObservableList<AdRow> data = FXCollections.observableArrayList();
        for (Object[] ad : Database.getActiveAds()) {
            data.add(new AdRow((int) ad[0], (String) ad[1], (String) ad[2], (double) ad[3]));
        }
        table.setItems(data);
    }

    private void makeOffer() {
        AdRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Veuillez sélectionner une annonce.");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Offre");
        dialog.setHeaderText("Annonce #" + selected.getId());
        dialog.setContentText("Montant proposé (€) :");
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                boolean saleConcluded = Database.makeOffer(selected.getId(), acheteur.getId(), amount);
                if (saleConcluded) {
                    alert("Félicitations ! Vente automatique conclue !");
                } else {
                    alert("Offre enregistrée. En attente d'une contre-proposition ou d'une autre offre.");
                }
                refreshAds(); // the ad may disappear if sold
            } catch (NumberFormatException ex) {
                alert("Montant invalide.");
            }
        });
    }

    private void alert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.show();
    }

    // Inner class for table row
    public static class AdRow {
        private final int id;
        private final String produit;
        private final String vendeur;
        private final double prixEstime;

        public AdRow(int id, String produit, String vendeur, double prixEstime) {
            this.id = id;
            this.produit = produit;
            this.vendeur = vendeur;
            this.prixEstime = prixEstime;
        }
        public int getId() { return id; }
        public String getProduit() { return produit; }
        public String getVendeur() { return vendeur; }
        public double getPrixEstime() { return prixEstime; }
    }

} */

