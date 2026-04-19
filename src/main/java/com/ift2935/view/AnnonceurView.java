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
import java.util.ArrayList;
import java.util.List;

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
        
        stage.setScene(new Scene(tabs, 850, 650));
        stage.setTitle("Espace Annonceur - " + user.getNom());
        stage.show();
    }

    private VBox createPublishTab() {
        VBox root = new VBox(20); root.setPadding(new Insets(30));
        GridPane grid = new GridPane(); grid.setHgap(10); grid.setVgap(15);
        TextField tit = new TextField(); TextField prx = new TextField();
        grid.add(new Label("Titre :"), 0, 0); grid.add(tit, 1, 0);
        grid.add(new Label("Prix souhaité (€) :"), 0, 1); grid.add(prx, 1, 1);

        Button btn = new Button("Soumettre le produit");
        btn.setOnAction(e -> {
            try {
                BigDecimal pS = new BigDecimal(prx.getText());
                // Afficher les infos de base avant expertise
                Alert summary = new Alert(Alert.AlertType.INFORMATION);
                summary.setTitle("Résumé");
                summary.setHeaderText("Soumission enregistrée");
                summary.setContentText("Produit: " + tit.getText() + "\nSouhaité: " + pS + " €");
                summary.showAndWait();

                // Simulation Expert
                TextInputDialog diag = new TextInputDialog("100.00");
                diag.setHeaderText("Un expert analyse le produit actuellement:");
                diag.setContentText("Prix estimé (€):");
                diag.showAndWait().ifPresent(val -> {
                    try {
                        Produit p = pService.soumettreProduit(user.getId(), 1, tit.getText(), "Standard", "Marque", "Model", "Bon", pS);
                        eService.createEstimation(p.getIdProduit(), 3, new BigDecimal(val), "Expertise simulée");
                        new Alert(Alert.AlertType.INFORMATION, "Expertise reçue. Veuillez décider dans l'onglet 2.").show();
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
        TableColumn<Estimation, Integer> col1 = new TableColumn<>("Produit ID");
        col1.setCellValueFactory(new PropertyValueFactory<>("id_produit"));
        TableColumn<Estimation, BigDecimal> col2 = new TableColumn<>("Prix Expert (€)");
        col2.setCellValueFactory(new PropertyValueFactory<>("prix_estime"));
        tableEstimations.getColumns().addAll(col1, col2);

        // ✅ Point 3: Les trois choix (Accepter, Refuser, Attente)
        Button btnAcc = new Button("ACCEPTER (Mettre en vente)");
        btnAcc.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnAcc.setPrefWidth(Double.MAX_VALUE);
        btnAcc.setOnAction(e -> handleDecision("acceptee"));

        Button btnRef = new Button("REFUSER (Supprimer)");
        btnRef.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        btnRef.setPrefWidth(Double.MAX_VALUE);
        btnRef.setOnAction(e -> handleDecision("refusee"));

        Button btnWait = new Button("GARDER EN ATTENTE");
        btnWait.setPrefWidth(Double.MAX_VALUE);
        btnWait.setOnAction(e -> handleDecision("en_attente"));

        root.getChildren().addAll(new Label("Estimations à traiter :"), tableEstimations, btnAcc, btnRef, btnWait);
        refresh();
        return root;
    }

    private void handleDecision(String dec) {
        Estimation sel = tableEstimations.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            if ("en_attente".equals(dec)) {
                new Alert(Alert.AlertType.INFORMATION, "Le produit reste en attente de décision.").show();
            } else {
                // ✅ Contrainte: La date de décision est générée par le service (Timestamp actuel)
                eService.decisionAnnonceur(sel.getId_estimation(), dec);
                if ("acceptee".equals(dec)) {
                    aService.publierAnnonce(sel.getId_produit(), null);
                    new Alert(Alert.AlertType.CONFIRMATION, "Succès! Produit en Vente (ACTIVE)").show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Expertise refusée. Le produit ne sera pas publié.").show();
                }
            }
            refresh();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Erreur : " + ex.getMessage()).show();
        }
    }

    private void refresh() {
        try {
            // ✅ Correction: On récupère les produits puis on cherche leurs estimations
            List<Produit> mesP = pService.getProduitsByAnnonceur(user.getId());
            List<Estimation> aTraiter = new ArrayList<>();
            for (Produit p : mesP) {
                try {
                    Estimation est = eService.getEstimationByProduit(p.getIdProduit());
                    if ("en_attente".equals(est.getDecision_annonceur())) aTraiter.add(est);
                } catch (Exception ignored) {}
            }
            tableEstimations.setItems(FXCollections.observableArrayList(aTraiter));
        } catch (Exception e) { e.printStackTrace(); }
    }
}
