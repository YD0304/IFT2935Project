package com.ift2935.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ift2935.DBConnection;
import com.ift2935.model.Annonce;

public class AnnonceDAO {

    public Annonce findById(int id) throws SQLException {
        String sql = "SELECT id_annonce, id_produit, date_publication, date_expiration, statut_annonce FROM Annonce WHERE id_annonce = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    public int insert(Annonce annonce) throws SQLException {
        String sql = "INSERT INTO Annonce (id_produit, date_publication, date_expiration, statut_annonce) VALUES (?,?,?,?) RETURNING id_annonce";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, annonce.getId_annonce());
            stmt.setTimestamp(2, new Timestamp(annonce.getDate_publication().getTime()));
            stmt.setTimestamp(3, annonce.getDate_expiration() != null ? new Timestamp(annonce.getDate_expiration().getTime()) : null);
            stmt.setString(4, annonce.getStatut_annonce());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Insert failed");
        }
    }

    public void updateStatut(int annonceId, String newStatut) throws SQLException {
        String sql = "UPDATE Annonce SET statut_annonce = ? WHERE id_annonce = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatut);
            stmt.setInt(2, annonceId);
            stmt.executeUpdate();
        }
    }

    public List<Annonce> getActiveAnnonces() throws SQLException {
        List<Annonce> list = new ArrayList<>();
        String sql = "SELECT * FROM Annonce WHERE statut_annonce = 'active' AND (date_expiration IS NULL OR date_expiration > NOW())";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // Transaction-aware version for service layer
    public void updateStatut(Connection conn, int annonceId, String newStatut) throws SQLException {
        String sql = "UPDATE Annonce SET statut_annonce = ? WHERE id_annonce = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatut);
            stmt.setInt(2, annonceId);
            stmt.executeUpdate();
        }
    }

    public Annonce findById(Connection conn, int id) throws SQLException {
        String sql = "SELECT id_annonce, id_produit, date_publication, date_expiration, statut_annonce FROM Annonce WHERE id_annonce = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    private Annonce map(ResultSet rs) throws SQLException {
        return new Annonce(
            rs.getInt("id_annonce"),
            rs.getInt("id_produit"),
            rs.getTimestamp("date_publication"),
            rs.getTimestamp("date_expiration"),
            rs.getString("statut_annonce")
        );
    }
}