package com.ift2935.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import com.ift2935.DBConnection;
import com.ift2935.dao.ProduitDAO;
import com.ift2935.model.Produit;

public class ExpertService {
    private static final Random random = new Random();

    public static BigDecimal simulerEstimation(int produitId) throws SQLException {
        ProduitDAO produitDAO = new ProduitDAO();
        Produit produit = produitDAO.findById(produitId);
        if (produit == null) throw new SQLException("Produit not found");

        int categorieId = produit.getIdCategorie();

        // Market average for same category (last 30 days)
        String sql = "SELECT AVG(v.prix_final) FROM Vente v " +
                     "JOIN Offre o ON v.id_offre = o.id_offre " +
                     "JOIN Annonce a ON o.id_annonce = a.id_annonce " +
                     "JOIN Produit p ON a.id_produit = p.id_produit " +
                     "WHERE p.id_categorie = ? AND v.date_vente > NOW() - INTERVAL '30 days'";
        BigDecimal moyenne = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categorieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) moyenne = rs.getBigDecimal(1);
        }

        BigDecimal estimation;
        if (moyenne != null && moyenne.compareTo(BigDecimal.ZERO) > 0) {
            double facteur = 0.9 + (random.nextDouble() * 0.2);
            estimation = moyenne.multiply(BigDecimal.valueOf(facteur));
        } else {
            double facteur = 0.8 + (random.nextDouble() * 0.4);
            estimation = produit.getPrixSouhaite().multiply(BigDecimal.valueOf(facteur));
        }
        if (estimation.compareTo(BigDecimal.ZERO) < 0) estimation = BigDecimal.ONE;
        return estimation;
    }
}