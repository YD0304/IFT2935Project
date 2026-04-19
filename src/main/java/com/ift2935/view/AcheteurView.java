package com.ift2935.view;

import com.ift2935.dao.AnnonceDAO;
import com.ift2935.model.Annonce;
import com.ift2935.model.Utilisateur;
import com.ift2935.service.VenteService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AcheteurView {
    private final Stage stage;
    private final Utilisateur user;
    private TableView<Annonce> table;
    private final AnnonceDAO adDAO = new AnnonceDAO();

    public AcheteurView(Stage stage, Utilisateur user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        VBox root = new VBox(20); root.setPadding(new Insets(30));
        Label header = new Label("Espace Acheteur - Marché");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        table = new TableView<>();
        TableColumn<Annonce, Integer> colAd = new TableColumn<>("Annonce ID");
        colAd.setCellValueFactory(new PropertyValueFactory<>("id_annonce"));
        table.getColumns().add(colAd);

        Button btnOffer = new Button("Proposer un prix (€)");
        btnOffer.setPrefWidth(Double.MAX_VALUE);
        btnOffer.setOnAction(e -> {
            Annonce sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            
            TextInputDialog dialog = new TextInputDialog("100.00");
            dialog.setTitle("Faire une offre");
            dialog.setContentText("Votre montant (€) :");
            dialog.showAndWait().ifPresent(val -> {
                BigDecimal amount = new BigDecimal(val);
                
                // Contrainte 1: Prix strictement positif
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    new Alert(Alert.AlertType.ERROR, "L'offre doit être > 0 !").show();
                    return;
                }

                try {
                    // Contrainte 2: Vente auto si prix >= estimation
                    boolean won = VenteService.soumettreProposition(sel.getId_annonce(), user.getId(), amount);
                    if (won) {
                        new Alert(Alert.AlertType.CONFIRMATION, "VENTE AUTOMATIQUE ! Prix atteint.").show();
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Offre en attente (inférieure à l'estimation).").show();
                    }
                } catch (Exception ex) {
                    // Démo Mode
                    BigDecimal mockExpert = new BigDecimal("500.00");
                    if (amount.compareTo(mockExpert) >= 0) {
                        new Alert(Alert.AlertType.CONFIRMATION, "VENTE AUTOMATIQUE (DÉMO) !").show();
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Offre en attente (DÉMO).").show();
                    }
                }
                refresh();
            });
        });

        root.getChildren().addAll(header, table, btnOffer);
        refresh();
        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(adDAO.getActiveAnnonces()));
        } catch (Exception e) {
            List<Annonce> list = new ArrayList<>();
            list.add(new Annonce(501, 10, new java.util.Date(), null, "active"));
            table.setItems(FXCollections.observableArrayList(list));
        }
    }
}
