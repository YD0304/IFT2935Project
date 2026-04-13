package com.ift2935;

import com.ift2935.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/vente_conditionnelle";
    private static final String USER = "postgres";
    private static final String PASSWORD = "yourpassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Login: returns Utilisateur (subclass instance)
    public static Utilisateur login(String email, String password) {
        String sql = "SELECT id, nom, prenom, email, mot_de_passe, type FROM utilisateur WHERE email = ? AND mot_de_passe = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String mail = rs.getString("email");
                String pwd = rs.getString("mot_de_passe");
                String type = rs.getString("type");
                switch (type) {
                    case "acheteur": return new Acheteur(id, nom, prenom, mail, pwd);
                    case "annonceur": return new Annonceur(id, nom, prenom, mail, pwd);
                    case "expert": return new Expert(id, nom, prenom, mail, pwd);
                    default: return null;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Get active ads for buyers
    public static List<Object[]> getActiveAds() {
        List<Object[]> ads = new ArrayList<>();
        String sql = "SELECT a.id_annonce, p.nom AS produit, u.nom AS vendeur, e.prix_estime " +
                     "FROM annonce a " +
                     "JOIN produit p ON a.produit_id = p.id_produit " +
                     "JOIN utilisateur u ON a.vendeur_id = u.id_utilisateur " +
                     "JOIN estimation e ON a.id_annonce = e.annonce_id " +
                     "WHERE a.statut = 'active' AND e.accepte_par_vendeur = true";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ads.add(new Object[]{
                    rs.getInt("id_annonce"),
                    rs.getString("produit"),
                    rs.getString("vendeur"),
                    rs.getDouble("prix_estime")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ads;
    }

    // Make an offer and auto-sell if >= estimate
    public static boolean makeOffer(int annonceId, int acheteurId, double montant) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Insert offer
            String sqlOffer = "INSERT INTO offre (annonce_id, acheteur_id, prix_propose) VALUES (?, ?, ?)";
            int offreId;
            try (PreparedStatement pst = conn.prepareStatement(sqlOffer, Statement.RETURN_GENERATED_KEYS)) {
                pst.setInt(1, annonceId);
                pst.setInt(2, acheteurId);
                pst.setDouble(3, montant);
                pst.executeUpdate();
                ResultSet keys = pst.getGeneratedKeys();
                keys.next();
                offreId = keys.getInt(1);
            }

            // Check if offer >= estimate
            String sqlCheck = "SELECT e.prix_estime FROM annonce a " +
                              "JOIN estimation e ON a.id_annonce = e.annonce_id " +
                              "WHERE a.id_annonce = ? AND e.accepte_par_vendeur = true";
            double estimate;
            try (PreparedStatement pst = conn.prepareStatement(sqlCheck)) {
                pst.setInt(1, annonceId);
                ResultSet rs = pst.executeQuery();
                rs.next();
                estimate = rs.getDouble("prix_estime");
            }

            if (montant >= estimate) {
                // Auto-sale
                String sqlSale = "INSERT INTO vente (annonce_id, offre_id, prix_final) VALUES (?, ?, ?)";
                try (PreparedStatement pst = conn.prepareStatement(sqlSale)) {
                    pst.setInt(1, annonceId);
                    pst.setInt(2, offreId);
                    pst.setDouble(3, montant);
                    pst.executeUpdate();
                }
                // Update ad status to 'vendue'
                String sqlUpdate = "UPDATE annonce SET statut = 'vendue' WHERE id_annonce = ?";
                try (PreparedStatement pst = conn.prepareStatement(sqlUpdate)) {
                    pst.setInt(1, annonceId);
                    pst.executeUpdate();
                }
                conn.commit();
                return true; // sale concluded
            } else {
                conn.commit();
                return false; // offer placed but no sale
            }
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }
}