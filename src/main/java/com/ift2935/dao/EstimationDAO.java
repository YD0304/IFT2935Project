package com.ift2935.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ift2935.DBConnection;
import com.ift2935.model.Estimation;

public class EstimationDAO {

    public Estimation findById(int id) throws SQLException {
        String sql = "SELECT id_estimation, id_produit, id_expert, prix_estime, commentaire, date_estimation, decision_annonceur, date_decision FROM Estimation WHERE id_estimation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    // Auto-commit version
    public int insert(Estimation estimation) throws SQLException {
        String sql = "INSERT INTO Estimation (id_produit, id_expert, prix_estime, commentaire, date_estimation, decision_annonceur, date_decision) VALUES (?,?,?,?,?,?,?) RETURNING id_estimation";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setInsertParameters(stmt, estimation);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Insert failed");
        }
    }

    // Transaction-aware version (called from services that manage their own connection)
    public int insert(Connection conn, Estimation estimation) throws SQLException {
        String sql = "INSERT INTO Estimation (id_produit, id_expert, prix_estime, commentaire, date_estimation, decision_annonceur, date_decision) VALUES (?,?,?,?,?,?,?) RETURNING id_estimation";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setInsertParameters(stmt, estimation);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Insert failed");
        }
    }

    private void setInsertParameters(PreparedStatement stmt, Estimation estimation) throws SQLException {
        stmt.setInt(1, estimation.getId_produit());
        stmt.setInt(2, estimation.getId_expert());
        stmt.setBigDecimal(3, estimation.getPrix_estime());
        stmt.setString(4, estimation.getCommentaire());
        stmt.setTimestamp(5, new Timestamp(estimation.getDate_estimation().getTime()));
        stmt.setString(6, estimation.getDecision_annonceur()); // 'en_attente'
        stmt.setTimestamp(7, estimation.getDateDecision() != null ? new Timestamp(estimation.getDateDecision().getTime()) : null);
    }

    public void updateDecision(int estimationId, String decision, Timestamp dateDecision) throws SQLException {
        String sql = "UPDATE Estimation SET decision_annonceur = ?, date_decision = ? WHERE id_estimation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, decision);
            stmt.setTimestamp(2, dateDecision);
            stmt.setInt(3, estimationId);
            stmt.executeUpdate();
        }
    }

    public void updateDecision(Connection conn, int estimationId, String decision, Timestamp dateDecision) throws SQLException {
        String sql = "UPDATE Estimation SET decision_annonceur = ?, date_decision = ? WHERE id_estimation = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, decision);
            stmt.setTimestamp(2, dateDecision);
            stmt.setInt(3, estimationId);
            stmt.executeUpdate();
        }
    }

    public Estimation findLatestByProduit(int produitId) throws SQLException {
        String sql = "SELECT id_estimation, id_produit, id_expert, prix_estime, commentaire, date_estimation, decision_annonceur, date_decision FROM Estimation WHERE id_produit = ? ORDER BY date_estimation DESC LIMIT 1";
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
            rs.getString("decision_annonceur"),   // ✅ fixed column name
            rs.getTimestamp("date_decision")
        );
    }
}
