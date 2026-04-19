package com.ift2935.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import com.ift2935.DBConnection;
import com.ift2935.dao.AnnonceDAO;
import com.ift2935.dao.OffreDAO;
import com.ift2935.dao.VenteDAO;
import com.ift2935.model.Offre;
import com.ift2935.model.Vente;

public class VenteService {

    private final VenteDAO venteDAO = new VenteDAO();
    private final OffreDAO offreDAO = new OffreDAO();
    private final AnnonceDAO annonceDAO = new AnnonceDAO();

    public Vente conclureVenteAutomatique(Offre offre, BigDecimal prixFinal) throws Exception {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Accept winning offer
            offre.setStatut_offre("acceptee");
            offreDAO.update(offre);

            // 2. Reject other pending offers on the same annonce
            rejectOtherOffers(conn, offre.getId_annonce(), offre.getId_offre());

            // 3. Close the announcement
            annonceDAO.updateStatut(conn, offre.getId_annonce(), "vendue");

            // 4. Record the sale
            Vente vente = new Vente(0, offre.getId_offre(), prixFinal, new Date(), "automatique");
            int venteId = venteDAO.insert(conn, vente);
            vente.setId_vente(venteId);

            conn.commit();
            return vente;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public Vente conclureVenteManuelle(Offre offre, BigDecimal prixFinal) throws Exception {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Accept winning offer
            offre.setStatut_offre("acceptee");
            offreDAO.update(offre);

            // 2. Reject other pending offers
            rejectOtherOffers(conn, offre.getId_annonce(), offre.getId_offre());

            // 3. Close the announcement
            annonceDAO.updateStatut(conn, offre.getId_annonce(), "vendue");

            // 4. Record the sale
            Vente vente = new Vente(0, offre.getId_offre(), prixFinal, new Date(), "manuelle");
            int venteId = venteDAO.insert(conn, vente);
            vente.setId_vente(venteId);

            conn.commit();
            return vente;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public Vente getVenteByAnnonce(int idAnnonce) throws Exception {
        // Find the accepted offer for this annonce
        for (Offre o : offreDAO.findByAnnonce(idAnnonce)) {
            if ("acceptee".equals(o.getStatut_offre())) {
                return venteDAO.findByOffreId(o.getId_offre());
            }
        }
        return null;
    }


    private void rejectOtherOffers(Connection conn, int idAnnonce, int winnerOffreId) throws Exception {
        for (Offre o : offreDAO.findByAnnonce(idAnnonce)) {
            if (o.getId_offre() != winnerOffreId && "en_attente".equals(o.getStatut_offre())) {
                o.setStatut_offre("refusee");
                offreDAO.update(o);
            }
        }
    }
}
