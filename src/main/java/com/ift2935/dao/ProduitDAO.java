package com.ift2935.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ift2935.DBConnection;
import com.ift2935.model.Produit;

public class ProduitDAO {

    public Produit findById(int id) throws SQLException {
        String sql = "SELECT id_produit, id_annonceur, id_categorie, titre, description, marque, modele, etat_produit, prix_souhaite, date_soumission " +
                     "FROM Produit WHERE id_produit = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    public int insert(Produit produit) throws SQLException {
        String sql = "INSERT INTO Produit (id_annonceur, id_categorie, titre, description, marque, modele, etat_produit, prix_souhaite, date_soumission) " +
                     "VALUES (?,?,?,?,?,?,?,?,?) RETURNING id_produit";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produit.getIdAnnonceur());
            stmt.setInt(2, produit.getIdCategorie());
            stmt.setString(3, produit.getTitre());
            stmt.setString(4, produit.getDescription());
            stmt.setString(5, produit.getMarque());
            stmt.setString(6, produit.getModele());
            stmt.setString(7, produit.getEtatProduit());
            stmt.setBigDecimal(8, produit.getPrix_souhaite());
            stmt.setTimestamp(9, new Timestamp(produit.getDate_soumission().getTime()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                produit.setIdProduit(id);
                return id;
            }
            throw new SQLException("Insert failed, no ID generated.");
        }
    }

    public List<Produit> findByAnnonceur(int annonceurId) throws SQLException {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT id_produit, id_annonceur, id_categorie, titre, description, marque, modele, etat_produit, prix_souhaite, date_soumission " +
                     "FROM Produit WHERE id_annonceur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, annonceurId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Produit> findWithoutEstimation() throws SQLException {
        String sql = "SELECT p.* FROM Produit p " +
                     "LEFT JOIN Estimation e ON p.id_produit = e.id_produit " +
                     "WHERE e.id_produit IS NULL " +
                     "ORDER BY p.date_soumission DESC";
        List<Produit> produits = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                produits.add(map(rs));
            }
        }
        return produits;
    }
    

    public void update(Produit produit) throws SQLException {
        String sql = "UPDATE Produit SET id_annonceur=?, id_categorie=?, titre=?, description=?, marque=?, modele=?, etat_produit=?, prix_souhaite=?, date_soumission=? " +
                     "WHERE id_produit=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produit.getIdAnnonceur());
            stmt.setInt(2, produit.getIdCategorie());
            stmt.setString(3, produit.getTitre());
            stmt.setString(4, produit.getDescription());
            stmt.setString(5, produit.getMarque());
            stmt.setString(6, produit.getModele());
            stmt.setString(7, produit.getEtatProduit());
            stmt.setBigDecimal(8, produit.getPrix_souhaite());
            stmt.setTimestamp(9, new Timestamp(produit.getDate_soumission().getTime()));
            stmt.setInt(10, produit.getIdProduit());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Update failed, Produit not found for id: " + produit.getIdProduit());
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Produit WHERE id_produit = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Delete failed, Produit not found for id: " + id);
            }
        }
    }

    private Produit map(ResultSet rs) throws SQLException {
        return new Produit(
            rs.getInt("id_produit"),
            rs.getInt("id_annonceur"),
            rs.getInt("id_categorie"),
            rs.getString("titre"),
            rs.getString("description"),
            rs.getString("marque"),
            rs.getString("modele"),
            rs.getString("etat_produit"),
            rs.getBigDecimal("prix_souhaite"),
            rs.getTimestamp("date_soumission")
        );
    }
}
