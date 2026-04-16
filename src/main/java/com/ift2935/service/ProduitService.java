package com.ift2935.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ift2935.DBConnection;
import com.ift2935.dao.AnnonceDAO;
import com.ift2935.dao.EstimationDAO;
import com.ift2935.dao.ProduitDAO;
import com.ift2935.model.Annonce;
import com.ift2935.model.Estimation;
import com.ift2935.model.Produit;

public class ProduitService {
    private final ProduitDAO produitDAO = new ProduitDAO();
    private final EstimationDAO estimationDAO = new EstimationDAO();
    private final AnnonceDAO annonceDAO = new AnnonceDAO();

    // 1. Soumettre un produit + estimation (expert auto)
    public Estimation soumettreProduitAvecEstimation(Produit produit, int expertId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int produitId = produitDAO.insert(produit);
            produit.setIdProduit(produitId);

            BigDecimal prixEstime = ExpertService.simulerEstimation(produitId);

            Estimation estimation = new Estimation(
                0, produitId, expertId, prixEstime,
                "Estimation automatique basée sur le marché récent",
                new Date(), "en_attente", null
            );
            int estimationId = estimationDAO.insert(estimation);
            estimation.setId_estimation(estimationId);

            conn.commit();
            return estimation;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    // 2. Récupérer les estimations en attente pour un annonceur
    public List<Estimation> getEstimationsEnAttentePourAnnonceur(int annonceurId) throws SQLException {
        List<Estimation> result = new ArrayList<>();
        String sql = "SELECT e.* FROM Estimation e " +
                     "JOIN Produit p ON e.id_produit = p.id_produit " +
                     "WHERE p.id_annonceur = ? AND e.decision = 'en_attente' " +
                     "ORDER BY e.date_estimation DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, annonceurId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapEstimation(rs));
                }
            }
        }
        return result;
    }

    // 3. Annonceur accepte l'estimation -> crée l'annonce active
    public void accepteEstimation(int estimationId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Update decision
            estimationDAO.updateDecision(estimationId, "acceptee", new Timestamp(new Date().getTime()));

            // Get the estimation to find produitId
            Estimation estimation = estimationDAO.findById(estimationId);
            if (estimation == null) throw new SQLException("Estimation introuvable");

            // Create active ad
            Annonce annonce = new Annonce(estimation.getId_produit(), 0, new Date(), null, "active");
            annonceDAO.insert(annonce);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    // 4. Annonceur refuse l'estimation
    public void refuseEstimation(int estimationId) throws SQLException {
        estimationDAO.updateDecision(estimationId, "refusee", new Timestamp(new Date().getTime()));
        // Optionally delete the product or mark it as refused
    }

    private Estimation mapEstimation(ResultSet rs) throws SQLException {
        return new Estimation(
            rs.getInt("id_estimation"),
            rs.getInt("id_produit"),
            rs.getInt("id_expert"),
            rs.getBigDecimal("prix_estime"),
            rs.getString("commentaire"),
            rs.getTimestamp("date_estimation"),
            rs.getString("decision"),
            rs.getTimestamp("date_decision")
        );
    }
}