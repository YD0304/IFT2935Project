package com.ift2935.view;

import com.ift2935.model.Estimation;
import com.ift2935.model.Produit;
import com.ift2935.model.Utilisateur;
import com.ift2935.service.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private final ProduitService produitService = new ProduitService();
    private TableView<Estimation> tableEstimations;
    private static final ObservableList<Estimation> demoList = FXCollections.observableArrayList();

    public AnnonceurView(Stage stage, Utilisateur user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        TabPane tabs = new TabPane();
        Tab t1 = new Tab("1. Publier Produit", createPub());
        Tab t2 = new Tab("2. Mes Décisions", createDec());
        t1.setClosable(false); t2.setClosable(false);
        tabs.getTabs().addAll(t1, t2);
        
        stage.setScene(new Scene(tabs, 700, 600));
        stage.setTitle("Espace Annonceur - " + user.getNom());
        stage.show();
    }

    private VBox createPub() {
        VBox root = new VBox(15); root.setPadding(new Insets(20));
        GridPane grid = new GridPane(); grid.setHgap(10); grid.setVgap(10);
        
        TextField tit = new TextField(); TextField prx = new TextField();
        grid.add(new Label("Titre:"), 0, 0); grid.add(tit, 1, 0);
        grid.add(new Label("Prix souhaité (€):"), 0, 1); grid.add(prx, 1, 1);

        Button btn = new Button("Soumettre");
        btn.setOnAction(e -> {
            BigDecimal price = new BigDecimal(prx.getText());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                new Alert(Alert.AlertType.WARNING, "Le prix doit être strictement positif !").show();
                return;
            }
            
            // Simulation Popup Expert
            TextInputDialog d = new TextInputDialog("100.00");
            d.setHeaderText("Estimation de l'expert");
            d.setContentText("Prix d'expertise proposé:");
            d.showAndWait().ifPresent(v -> {
                BigDecimal expVal = new BigDecimal(v);
                Estimation est = new Estimation(0, 0, 3, expVal, "Expertise automatique", new Date(), "en_attente", null);
                demoList.add(est);
                try {
                    Produit p = new Produit(0, user.getId(), 1, tit.getText(), "", "", "", "Bon", price, new Date());
                    produitService.soumettreProduitAvecEstimation(p, 3);
                } catch (Exception ex) { System.err.println("Database hors ligne, utilisation de la liste démo."); }
                new Alert(Alert.AlertType.INFORMATION, "Produit soumis. Allez dans l'onglet 2 pour valider.").show();
                refresh();
            });
        });
        root.getChildren().addAll(new Label("Vendre un nouvel objet"), grid, btn);
        return root;
    }

    private VBox createDec() {
        VBox root = new VBox(10); root.setPadding(new Insets(20));
        tableEstimations = new TableView<>();
        TableColumn<Estimation, BigDecimal> c1 = new TableColumn<>("Prix Expert (€)");
        c1.setCellValueFactory(new PropertyValueFactory<>("prix_estime"));
        tableEstimations.getColumns().add(c1);

        Button btnAcc = new Button("ACCEPTER"); btnAcc.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        Button btnRef = new Button("REFUSER"); btnRef.setStyle("-fx-background-color: red; -fx-text-fill: white;");

        btnAcc.setOnAction(e -> handle(true));
        btnRef.setOnAction(e -> handle(false));

        root.getChildren().addAll(new Label("Estimations reçues :"), tableEstimations, btnAcc, btnRef);
        refresh();
        return root;
    }

    private void handle(boolean acc) {
        Estimation sel = tableEstimations.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        
        // Contrainte: Date décision = maintenant
        sel.setDateDecision(new Date()); 
        sel.setDecision(acc ? "acceptee" : "refusee");
        
        try {
            if (acc) produitService.accepteEstimation(sel.getId_estimation());
            else produitService.refuseEstimation(sel.getId_estimation());
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "Décision '" + sel.getDecision() + "' enregistrée à la date du " + sel.getDateDecision()).show();
        }
        demoList.remove(sel);
        refresh();
    }

    private void refresh() {
        try {
            tableEstimations.setItems(FXCollections.observableArrayList(produitService.getEstimationsEnAttentePourAnnonceur(user.getId())));
        } catch (Exception e) { tableEstimations.setItems(demoList); }
    }
}
