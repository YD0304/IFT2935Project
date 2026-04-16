package com.ift2935.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.ift2935.DBConnection;
import com.ift2935.dao.AnnonceDAO;
import com.ift2935.dao.EstimationDAO;
import com.ift2935.dao.OffreDAO;
import com.ift2935.dao.VenteDAO;
import com.ift2935.model.Annonce;
import com.ift2935.model.Estimation;
import com.ift2935.model.Offre;
import com.ift2935.model.Vente;

public class VenteService {

    public static boolean soumettreProposition(int annonceId, int acheteurId, BigDecimal montant) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            AnnonceDAO annonceDAO = new AnnonceDAO();
            Annonce annonce = annonceDAO.findById(conn, annonceId);
            if (annonce == null || !"active".equals(annonce.getStatut_annonce())) {
                conn.rollback();
                return false;
            }

            // Get product id from annonce
            int produitId = annonce.getId_produit();

            EstimationDAO estimationDAO = new EstimationDAO();
            Estimation estimation = estimationDAO.findLatestByProduit(produitId);
            if (estimation == null || !"acceptee".equals(estimation.getDecision())) {
                conn.rollback();
                return false;
            }

            BigDecimal prixEstime = estimation.getPrix_estime();

            Offre offre = new Offre(annonceId, acheteurId, produitId, montant, new Date(), "en_attente");
        

            OffreDAO offreDAO = new OffreDAO();
            int offreId = offreDAO.insert(conn, offre);

            boolean venteConclue = false;
            if (montant.compareTo(prixEstime) >= 0) {
                Vente vente = new Vente(offreId, annonceId, montant, new Date(), "automatique");

                VenteDAO venteDAO = new VenteDAO();
                venteDAO.insert(conn, vente);

                annonceDAO.updateStatut(conn, annonceId, "vendue");
                venteConclue = true;
            }

            conn.commit();
            return venteConclue;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
}