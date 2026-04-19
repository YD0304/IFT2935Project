package com.ift2935.view;

import com.ift2935.model.Estimation;
import com.ift2935.model.Produit;
import com.ift2935.model.Utilisateur;
import com.ift2935.service.AnnonceService;
import com.ift2935.service.EstimationService;
import com.ift2935.service.ProduitService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.Date;

public class AnnonceurView {
    private final Stage stage;
    private final Utilisateur user;
    private final ProduitService pService = new ProduitService();
    private final EstimationService eService = new EstimationService();
    private final AnnonceService aService = new AnnonceService();
    private TableView<Estimation> tableEstimations;

    public AnnonceurView(Stage stage, Utilisateur user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("1. Publier Produit", createPublishTab()));
        tabs.getTabs().add(new Tab("2. Décisions Expertise", createDecisionTab()));
        
        stage.setTitle("Espace Annonceur - " + user.getNom());
        stage.setScene(new Scene(tabs, 850, 650));
        stage.show();
    }

    private VBox createPublishTab() {
        VBox root = new VBox(20); root.setPadding(new Insets(30));
        GridPane grid = new GridPane(); grid.setHgap(10); grid.setVgap(15);

        TextField txtTitre = new TextField();
        TextField txtPrix = new TextField();
        grid.add(new Label("Titre :"), 0, 0); grid.add(txtTitre, 1, 0);
        grid.add(new Label("Prix souhaité (€) :"), 0, 1); grid.add(txtPrix, 1, 1);

        Button btn = new Button("Soumettre pour expertise");
        btn.setOnAction(e -> {
            try {
                BigDecimal pS = new BigDecimal(txtPrix.getText());
                // 1. Soumettre le produit
                Produit p = pService.soumettreProduit(user.getId(), 1, txtTitre.getText(), "Desc", "Marque", "Modele", "Bon", pS);
                
                // 2. SIMULATION EXPERT (Immédiat selon PDF)
                TextInputDialog diag = new TextInputDialog("100.00");
                diag.setHeaderText("SIMULATION EXPERT : L'expert analyse l'objet.");
                diag.setContentText("Prix d'expertise (€) :");
                diag.showAndWait().ifPresent(val -> {
                    try {
                        eService.createEstimation(p.getIdProduit(), 3, new BigDecimal(val), "Expertise simulée");
                        new Alert(Alert.AlertType.INFORMATION, "Produit soumis. Allez dans l'onglet 2 pour décider.").show();
                        refresh();
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Données invalides.").show();
            }
        });
        root.getChildren().addAll(new Label("Publier un nouvel article"), grid, btn);
        return root;
    }

    private VBox createDecisionTab() {
        VBox root = new VBox(15); root.setPadding(new Insets(20));
        tableEstimations = new TableView<>();
        TableColumn<Estimation, Integer> colP = new TableColumn<>("Produit ID");
        colP.setCellValueFactory(new PropertyValueFactory<>("id_produit"));
        TableColumn<Estimation, BigDecimal> colV = new TableColumn<>("Prix Expert (€)");
        colV.setCellValueFactory(new PropertyValueFactory<>("prix_estime"));
        tableEstimations.getColumns().addAll(colP, colV);

        Button btnAcc = new Button("ACCEPTER (Publier l'Annonce)");
        btnAcc.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnAcc.setOnAction(e -> handleDecision("acceptee"));

        Button btnRef = new Button("REFUSER");
        btnRef.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        btnRef.setOnAction(e -> handleDecision("refusee"));

        root.getChildren().addAll(new Label("Estimations en attente :"), tableEstimations, btnAcc, btnRef);
        refresh();
        return root;
    }

    private void handleDecision(String dec) {
        Estimation sel = tableEstimations.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            eService.decisionAnnonceur(sel.getId_estimation(), dec);
            if ("acceptee".equals(dec)) {
                // Point 5: Publication auto si acceptée
                aService.publierAnnonce(sel.getId_produit(), null); 
                new Alert(Alert.AlertType.CONFIRMATION, "Annonce publiée avec succès !").show();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Expertise refusée.").show();
            }
            refresh();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erreur : " + ex.getMessage()).show();
        }
    }

    private void refresh() {
        try {
            // Uniquement les estimations 'en_attente' pour cet annonceur
            tableEstimations.setItems(FXCollections.observableArrayList(eService.getEstimationByProduit(99))); // Demo placeholder
        } catch (Exception e) {}
    }
}
