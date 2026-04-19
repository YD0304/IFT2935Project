package com.ift2935.view;

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

public class AcheteurView {
    private final Stage stage;
    private final Utilisateur user;
    private final AnnonceService aService = new AnnonceService();
    private final OffreService oService = new OffreService();
    private final VenteService vService = new VenteService();
    private TableView<Annonce> table;

    public AcheteurView(Stage stage, Utilisateur user) {
        this.stage = stage;
        this.user = user;
    }

    public void show() {
        VBox root = new VBox(20); root.setPadding(new Insets(30));
        Label header = new Label("Espace Acheteur : Produits Disponibles");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        table = new TableView<>();
        TableColumn<Annonce, Integer> c1 = new TableColumn<>("Annonce ID");
        c1.setCellValueFactory(new PropertyValueFactory<>("id_annonce"));
        TableColumn<Annonce, String> c2 = new TableColumn<>("Statut");
        c2.setCellValueFactory(new PropertyValueFactory<>("statut_annonce"));
        table.getColumns().addAll(c1, c2);

        Button btnBid = new Button("Faire une offre (€)");
        btnBid.setPrefWidth(Double.MAX_VALUE);
        btnBid.setOnAction(e -> {
            Annonce sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) return;

            TextInputDialog d = new TextInputDialog("100.00");
            d.setHeaderText("Objet #" + sel.getId_produit());
            d.setContentText("Votre prix (€) :");
            d.showAndWait().ifPresent(val -> {
                try {
                    BigDecimal amt = new BigDecimal(val);
                    // Point 6: Appel de la logique de C (Vente automatique intégrée)
                    oService.faireOffre(sel.getId_annonce(), user.getId(), amt);
                    
                    // Vérifier si une vente a été créée
                    if (vService.getVenteByAnnonce(sel.getId_annonce()) != null) {
                        new Alert(Alert.AlertType.CONFIRMATION, "VENTE AUTOMATIQUE ! Votre prix a été accepté.").show();
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Offre enregistrée (En attente).").show();
                    }
                    refresh();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, "Transaction impossible : " + ex.getMessage()).show();
                }
            });
        });

        root.getChildren().addAll(header, table, btnBid);
        refresh();
        stage.setScene(new Scene(root, 650, 500));
        stage.show();
    }

    private void refresh() {
        try {
            // Point 5: Seules les annonces 'active' sont affichées
            table.setItems(FXCollections.observableArrayList(aService.getActiveAnnonces()));
        } catch (Exception e) {}
    }
}
