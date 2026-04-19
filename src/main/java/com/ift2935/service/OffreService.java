package com.ift2935.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ift2935.dao.AnnonceDAO;
import com.ift2935.dao.EstimationDAO;
import com.ift2935.dao.OffreDAO;
import com.ift2935.model.Annonce;
import com.ift2935.model.Estimation;
import com.ift2935.model.Offre;
import com.ift2935.model.Vente;

public class OffreService {

    private final OffreDAO offreDAO = new OffreDAO();
    private final AnnonceDAO annonceDAO = new AnnonceDAO();
    private final EstimationDAO estimationDAO = new EstimationDAO();
    private final VenteService venteService = new VenteService();

    /**
     * A buyer submits an offer on an active announcement.
     *
     * Business rules enforced:
     *  - The announcement must be active.
     *  - The offer amount must be positive.
     *  - If montant >= prix_estime (expert estimate), the sale is concluded
     *    automatically via VenteService (mode_conclusion = 'automatique').
     *
     * @return the persisted Offre (statut may already be 'acceptee' if auto-sale fired)
     */
    public Offre faireOffre(int idAnnonce, int idAcheteur, BigDecimal montant) throws Exception {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Le montant de l'offre doit être positif.");

        Annonce annonce = annonceDAO.findById(idAnnonce);
        if (annonce == null)
            throw new IllegalArgumentException("Annonce introuvable (id=" + idAnnonce + ").");
        if (!"active".equals(annonce.getStatut_annonce()))
            throw new IllegalStateException("L'annonce n'est plus active. Impossible de soumettre une offre.");

        // 2. Persist the offer (statut = 'en_attente')
        Offre offre = new Offre(0, idAnnonce, idAcheteur, montant, new Date(), "en_attente");
        int id = offreDAO.insert(offre);   // see note below *
        offre.setId_offre(id);

        // 3. Auto-sale: check against expert estimate
        Estimation estimation = estimationDAO.findLatestByProduit(annonce.getId_produit());
        if (estimation != null
                && "acceptee".equals(estimation.getDecision_annonceur())
                && montant.compareTo(estimation.getPrix_estime()) >= 0) {
            // Trigger automatic sale – VenteService handles the full transaction
            venteService.conclureVenteAutomatique(offre, montant);
        }

        return offre;
    }

    /**
     * The announcer manually accepts a specific pending offer.
     * Delegates to VenteService which wraps everything in one transaction.
     *
     * @return the concluded Vente
     */
    public Vente accepterOffre(int idOffre) throws Exception {
        Offre offre = offreDAO.findById(idOffre);
        if (offre == null)
            throw new IllegalArgumentException("Offre introuvable (id=" + idOffre + ").");
        if (!"en_attente".equals(offre.getStatut_offre()))
            throw new IllegalStateException("Cette offre n'est plus en attente.");

        // Verify the annonce is still active
        Annonce annonce = annonceDAO.findById(offre.getId_annonce());
        if (annonce == null || !"active".equals(annonce.getStatut_annonce()))
            throw new IllegalStateException("L'annonce associée n'est plus active.");

        return venteService.conclureVenteManuelle(offre, offre.getMontant_offre());
    }

    /**
     * The announcer manually refuses a specific pending offer.
     * Sets statut = 'refusee'.
     */
    public void refuserOffre(int idOffre) throws Exception {
        Offre offre = offreDAO.findById(idOffre);
        if (offre == null)
            throw new IllegalArgumentException("Offre introuvable (id=" + idOffre + ").");
        if (!"en_attente".equals(offre.getStatut_offre()))
            throw new IllegalStateException("Cette offre ne peut plus être refusée (statut: " + offre.getStatut_offre() + ").");

        offre.setStatut_offre("refusee");
        offreDAO.update(offre);
    }

    /**
     * Returns all offers for a given announcement, sorted by amount descending.
     */
    public List<Offre> getOffresByAnnonce(int idAnnonce) throws Exception {
        return offreDAO.findByAnnonce(idAnnonce);
    }
}