package com.ift2935;

import java.math.BigDecimal;

import com.ift2935.model.Annonce;
import com.ift2935.model.Estimation;
import com.ift2935.model.Offre;
import com.ift2935.model.Produit;
import com.ift2935.model.Vente;
import com.ift2935.service.AnnonceService;
import com.ift2935.service.EstimationService;
import com.ift2935.service.OffreService;
import com.ift2935.service.ProduitService;
import com.ift2935.service.VenteService;

public class TestService {
            public static void main(String[] args) {
            try {
                // ---- Setup: clean IDs (adjust based on your existing data) ----
                int annonceurId = 1;      // existing announcer in your DB
                int expertId = 2;          // existing expert
                int acheteurId = 3;        // existing buyer
                int categorieId = 1;       // existing category
    
                ProduitService produitService = new ProduitService();
                EstimationService estimationService = new EstimationService();
                AnnonceService annonceService = new AnnonceService();
                OffreService offreService = new OffreService();
    
                // 1. Announcer submits a product
                System.out.println("1. Submitting product...");
                Produit produit = produitService.soumettreProduit(
                    annonceurId, categorieId,
                    "MacBook Pro 2023", "Excellent condition", "Apple", "M3 Pro",
                    "like new", new BigDecimal("2200")
                );
                System.out.println("   Product created with ID: " + produit.getIdProduit());
    
                // 2. Expert estimates the product
                System.out.println("\n2. Expert gives estimation...");
                Estimation estimation = estimationService.createEstimation(
                    produit.getIdProduit(), expertId,
                    new BigDecimal("2000"), "Market price is around 2000 CAD"
                );
                System.out.println("   Estimation ID: " + estimation.getId_estimation() + 
                                   ", price: " + estimation.getPrix_estime());
    
                // 3. Announcer accepts the estimation
                System.out.println("\n3. Announcer accepts estimation...");
                estimationService.decisionAnnonceur(estimation.getId_estimation(), "acceptee");
                System.out.println("   Estimation accepted.");
    
                // 4. Publish the announcement
                System.out.println("\n4. Publishing announcement...");
                Annonce annonce = annonceService.publierAnnonce(produit.getIdProduit(), null);
                System.out.println("   Announcement ID: " + annonce.getId_annonce() + 
                                   ", status: " + annonce.getStatut_annonce());
    
                // 5. Buyer makes an offer (below expert estimate → no auto-sale)
                System.out.println("\n5. Buyer makes offer below estimate (1900)...");
                Offre offre1 = offreService.faireOffre(annonce.getId_annonce(), acheteurId, new BigDecimal("1900"));
                System.out.println("   Offer status: " + offre1.getStatut_offre() + " (still pending)");
    
                // 6. Another buyer makes offer >= estimate → automatic sale
                System.out.println("\n6. Buyer makes offer >= estimate (2100)...");
                Offre offre2 = offreService.faireOffre(annonce.getId_annonce(), acheteurId, new BigDecimal("2100"));
                System.out.println("   Offer status: " + offre2.getStatut_offre() + " (accepted automatically)");
    
                // 7. Check that announcement is now closed
                System.out.println("\n7. Checking announcement status after auto-sale...");
                Annonce closedAnnonce = annonceService.getActiveAnnonces().stream()
                    .filter(a -> a.getId_annonce() == annonce.getId_annonce())
                    .findFirst()
                    .orElse(null);
                if (closedAnnonce == null) {
                    System.out.println("   Announcement is no longer active (correct, it should be 'vendue')");
                }
    
                // 8. (Optional) Retrieve sale information
                VenteService venteService = new VenteService();
                Vente vente = venteService.getVenteByAnnonce(annonce.getId_annonce());
                if (vente != null) {
                    System.out.println("\n8. Sale recorded with ID: " + vente.getId_vente() +
                                       ", final price: " + vente.getPrix_final() +
                                       ", mode: " + vente.getMode_conclusion());
                }
    
                System.out.println("\n=== All tests passed (no exceptions) ===");
    
            } catch (Exception e) {
                System.err.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
