package com.ift2935.service;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.ift2935.DBConnection;
import com.ift2935.dao.AnnonceDAO;
import com.ift2935.dao.EstimationDAO;
import com.ift2935.model.Annonce;
import com.ift2935.model.Estimation;

public class AnnonceService {

    private final AnnonceDAO annonceDAO = new AnnonceDAO();
    private final EstimationDAO estimationDAO = new EstimationDAO();

    /**
     * Publishes an announcement for a product whose estimation has been accepted.
     *
     * Workflow:
     *  1. Verify that the latest estimation for this product was accepted.
     *  2. Insert a new Annonce with statut = 'active'.
     *
     * @param idProduit      product to announce
     * @param dateExpiration optional expiry date (null = no expiry)
     * @return the created Annonce
     */
    public Annonce publierAnnonce(int idProduit, Date dateExpiration) throws Exception {
        // Guard: estimation must have been accepted
        Estimation estimation = estimationDAO.findLatestByProduit(idProduit);
        if (estimation == null)
            throw new IllegalStateException("Aucune estimation trouvée pour ce produit.");
        if (!"acceptee".equals(estimation.getDecision_annonceur()))
            throw new IllegalStateException("L'annonceur doit accepter l'estimation avant de publier.");

        Annonce annonce = new Annonce(
            0, idProduit,
            new Date(),
            dateExpiration,
            "active"
        );

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            int id = annonceDAO.insert(conn, annonce);
            annonce.setId_annonce(id);
            conn.commit();
            return annonce;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Lets the announcer manually withdraw an active announcement.
     * Sets statut = 'retiree'.
     */
    public void retirerAnnonce(int idAnnonce) throws Exception {
        Annonce annonce = annonceDAO.findById(idAnnonce);
        if (annonce == null)
            throw new IllegalArgumentException("Annonce introuvable (id=" + idAnnonce + ").");
        if (!"active".equals(annonce.getStatut_annonce()))
            throw new IllegalStateException("Seule une annonce active peut être retirée.");

        annonceDAO.updateStatut(idAnnonce, "retiree");
    }

    /**
     * Marks an announcement as expired (called by a scheduler or manually).
     * Sets statut = 'expiree'.
     */
    public void expireAnnonce(int idAnnonce) throws Exception {
        Annonce annonce = annonceDAO.findById(idAnnonce);
        if (annonce == null)
            throw new IllegalArgumentException("Annonce introuvable (id=" + idAnnonce + ").");
        if (!"active".equals(annonce.getStatut_annonce()))
            throw new IllegalStateException("Seule une annonce active peut être expirée.");

        // Verify the expiry date has actually passed
        if (annonce.getDate_expiration() != null
                && annonce.getDate_expiration().after(new Date()))
            throw new IllegalStateException("La date d'expiration n'est pas encore atteinte.");

        annonceDAO.updateStatut(idAnnonce, "expiree");
    }

    /**
     * Returns all currently active announcements visible to buyers.
     */
    public List<Annonce> getActiveAnnonces() throws Exception {
        return annonceDAO.getActiveAnnonces();
    }

    /**
     * Returns all announcements published by a specific announcer (for management).
     */
    public List<Annonce> getAnnoncesByAnnonceur(int annonceurId) throws Exception {
        return annonceDAO.findByAnnonceur(annonceurId);
    }
}