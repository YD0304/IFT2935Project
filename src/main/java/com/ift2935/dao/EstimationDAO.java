package com.ift2935.dao;

import com.ift2935.DBConnection;
import com.ift2935.model.Estimation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstimationDAO {

    public Estimation findById(int id) throws SQLException {
        String sql = "SELECT * FROM Estimation WHERE id_estimation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    public int insert(Estimation estimation) throws SQLException {
        String sql = "INSERT INTO Estimation (id_produit, id_expert, prix_estime, commentaire, date_estimation, decision, date_decision) VALUES (?,?,?,?,?,?,?) RETURNING id_estimation";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, estimation.getId_produit());
            stmt.setInt(2, estimation.getId_expert());
            stmt.setBigDecimal(3, estimation.getPrix_estime());
            stmt.setString(4, estimation.getCommentaire());
            stmt.setTimestamp(5, new Timestamp(estimation.getDate_estimation().getTime()));
            stmt.setString(6, estimation.getDecision()); // 'en_attente' initially
            stmt.setTimestamp(7, estimation.getDateDecision() != null ? new Timestamp(estimation.getDateDecision().getTime()) : null);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Insert failed");
        }
    }

    public void updateDecision(int estimationId, String decision, Timestamp dateDecision) throws SQLException {
        String sql = "UPDATE Estimation SET decision = ?, date_decision = ? WHERE id_estimation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, decision);
            stmt.setTimestamp(2, dateDecision);
            stmt.setInt(3, estimationId);
            stmt.executeUpdate();
        }
    }

    public Estimation findLatestByProduit(int produitId) throws SQLException {
        String sql = "SELECT * FROM Estimation WHERE id_produit = ? ORDER BY date_estimation DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    private Estimation map(ResultSet rs) throws SQLException {
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