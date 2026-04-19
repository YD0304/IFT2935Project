package com.ift2935.view;

import com.ift2935.dao.AnnonceDAO;
import com.ift2935.model.Annonce;
import com.ift2935.model.Utilisateur;
import com.ift2935.service.AnnonceService;
import com.ift2935.service.OffreService;
import com.ift2935.service.VenteService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.List;

public class AcheteurView {
    private final Stage stage;
    private final Utilisateur user;
    private final AnnonceDAO adDAO = new AnnonceDAO();
    private final OffreService oService = new OffreService();
    private final VenteService vService = new VenteService();
    private TableView<Annonce> table;

    public AcheteurView(Stage stage, Utilisateur user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        VBox root = new VBox(20); root.setPadding(new Insets(30));
        Label h = new Label("Espace Acheteur: Produits en Vente");
        h.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        table = new TableView<>();
        TableColumn<Annonce, Integer> colAd = new TableColumn<>("ID Annonce");
        colAd.setCellValueFactory(new PropertyValueFactory<>("id_annonce"));
        
        // ✅ Point 4: Affichage du statut (active, vendue, expiree, retiree)
        TableColumn<Annonce, String> colSt = new TableColumn<>("Statut actuel");
        colSt.setCellValueFactory(new PropertyValueFactory<>("statut_annonce"));
        
        table.getColumns().addAll(colAd, colSt);
        table.setPrefHeight(400);

        Button btnBid = new Button("Faire une offre de prix (€)");
        btnBid.setPrefWidth(Double.MAX_VALUE);
        btnBid.setOnAction(e -> {
            Annonce sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            
            // Contrainte: Uniquement sur les annonces actives
            if (!"active".equals(sel.getStatut_annonce())) {
                new Alert(Alert.AlertType.ERROR, "Offres interdites sur un produit " + sel.getStatut_annonce()).show();
                return;
            }

            TextInputDialog d = new TextInputDialog("100.00");
            d.setHeaderText("Offre pour le produit #" + sel.getId_produit());
            d.setContentText("Montant (€) :");
            d.showAndWait().ifPresent(val -> {
                try {
                    BigDecimal amt = new BigDecimal(val);
                    // Logic automatique si montant >= expertise
                    oService.faireOffre(sel.getId_annonce(), user.getId(), amt);
                    
                    if (vService.getVenteByAnnonce(sel.getId_annonce()) != null) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Félicitations! VENTE AUTOMATIQUE CONCLUE!").show();
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Offre enregistrée. En attente de la décision du vendeur.").show();
                    }
                    refresh();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Erreur : " + ex.getMessage()).show();
                }
            });
        });

        root.getChildren().addAll(h, new Label("Note : Vous voyez tout, mais ne pouvez acheter que les produits 'active'."), table, btnBid);
        refresh();
        stage.setScene(new Scene(root, 700, 600));
        stage.setTitle("Catalogue - Acheteur");
        stage.show();
    }

    private void refresh() {
        try {
            // ✅ Charger TOUTES les annonces de la DB (findAll)
            List<Annonce> allAds = adDAO.findAll();
            table.setItems(FXCollections.observableArrayList(allAds));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
