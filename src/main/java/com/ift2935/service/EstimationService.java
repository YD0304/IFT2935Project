package com.ift2935.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.ift2935.dao.EstimationDAO;
import com.ift2935.model.Estimation;

public class EstimationService {

    private final EstimationDAO estimationDAO = new EstimationDAO();

    /**
     * Records an expert's price estimate for a product.
     * Called after an expert evaluates a submitted product.
     *
     * @param idProduit   product being estimated
     * @param idExpert    expert performing the estimation
     * @param prixEstime  estimated market price
     * @param commentaire optional expert comment
     * @return the persisted Estimation with its generated ID
     */
    public Estimation createEstimation(int idProduit, int idExpert,
                                       BigDecimal prixEstime, String commentaire) throws Exception {
        if (prixEstime == null || prixEstime.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Le prix estimé doit être positif.");

        Estimation estimation = new Estimation(
            0, idProduit, idExpert, prixEstime,
            commentaire, new Date(), "en_attente", null
        );
        int id = estimationDAO.insert(estimation);
        estimation.setId_estimation(id);
        return estimation;
    }

    /**
     * Returns the most recent estimation for a given product.
     * (Visible only to authorized roles — not to buyers.)
     */
    public Estimation getEstimationByProduit(int idProduit) throws Exception {
        Estimation e = estimationDAO.findLatestByProduit(idProduit);
        if (e == null)
            throw new IllegalStateException("Aucune estimation trouvée pour le produit id=" + idProduit + ".");
        return e;
    }

    /**
     * Records the announcer's decision on the expert estimation.
     *
     * Decision values: "acceptee" | "refusee"
     *
     * If accepted → the caller (AnnonceService) should publish the announcement.
     * If refused  → product stays in pending state; no announcement is created.
     *
     * @return the updated Estimation
     */
    public Estimation decisionAnnonceur(int idEstimation, String decision) throws Exception {
        if (!decision.equals("acceptee") && !decision.equals("refusee"))
            throw new IllegalArgumentException("Décision invalide. Valeurs acceptées: 'acceptee' ou 'refusee'.");

        Estimation estimation = estimationDAO.findById(idEstimation);
        if (estimation == null)
            throw new IllegalArgumentException("Estimation introuvable (id=" + idEstimation + ").");
        if (!"en_attente".equals(estimation.getDecision_annonceur()))
            throw new IllegalStateException("Cette estimation a déjà été traitée.");

        Timestamp now = new Timestamp(System.currentTimeMillis());
        estimationDAO.updateDecision(idEstimation, decision, now);

        estimation.setDecision_annonceur(decision);
        estimation.setDateDecision(now);
        return estimation;
    }
}