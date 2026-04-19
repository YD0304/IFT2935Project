package com.ift2935.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ift2935.DBConnection;
import com.ift2935.model.Vente;

public class VenteDAO {

    // ========== Basic CRUD ==========

    public Vente findById(int id) throws SQLException {
        String sql = "SELECT id_vente, id_offre, prix_final, date_vente, mode_conclusion " +
                     "FROM Vente WHERE id_vente = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Vente(
                    rs.getInt("id_vente"),
                    rs.getInt("id_offre"),
                    rs.getBigDecimal("prix_final"),
                    rs.getTimestamp("date_vente"),
                    rs.getString("mode_conclusion")
                );
            }
            return null;
        }
    }

    public Vente findByOffreId(int idOffre) throws SQLException {
        String sql = "SELECT id_vente, id_offre, prix_final, date_vente, mode_conclusion " +
                     "FROM Vente WHERE id_offre = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOffre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Vente(
                    rs.getInt("id_vente"),
                    rs.getInt("id_offre"),
                    rs.getBigDecimal("prix_final"),
                    rs.getTimestamp("date_vente"),
                    rs.getString("mode_conclusion")
                );
            }
            return null;
        }
    }

    // Returns generated id_vente
    public int insert(Vente vente) throws SQLException {
        String sql = "INSERT INTO Vente (id_offre, prix_final, date_vente, mode_conclusion) " +
                     "VALUES (?, ?, ?, ?) RETURNING id_vente";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vente.getId_offre());
            stmt.setBigDecimal(2, vente.getPrix_final());
            stmt.setTimestamp(3, new Timestamp(vente.getDate_vente().getTime()));
            stmt.setString(4, vente.getMode_conclusion());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                vente.setId_vente(generatedId);  // update model object
                return generatedId;
            }
            throw new SQLException("Insert failed, no ID generated.");
        }
    }

    // Transaction-aware version (use when sharing a connection)
    public int insert(Connection conn, Vente vente) throws SQLException {
        String sql = "INSERT INTO Vente (id_offre, prix_final, date_vente, mode_conclusion) " +
                     "VALUES (?, ?, ?, ?) RETURNING id_vente";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vente.getId_offre());
            stmt.setBigDecimal(2, vente.getPrix_final());
            stmt.setTimestamp(3, new Timestamp(vente.getDate_vente().getTime()));
            stmt.setString(4, vente.getMode_conclusion());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                vente.setId_vente(generatedId);
                return generatedId;
            }
            throw new SQLException("Insert failed, no ID generated.");
        }
    }

    public void update(Vente vente) throws SQLException {
        String sql = "UPDATE Vente SET id_offre=?, prix_final=?, date_vente=?, mode_conclusion=? " +
                     "WHERE id_vente=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vente.getId_offre());
            stmt.setBigDecimal(2, vente.getPrix_final());
            stmt.setTimestamp(3, new Timestamp(vente.getDate_vente().getTime()));
            stmt.setString(4, vente.getMode_conclusion());
            stmt.setInt(5, vente.getId_vente());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Update failed, Vente not found for id: " + vente.getId_vente());
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Vente WHERE id_vente = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Delete failed, Vente not found for id: " + id);
            }
        }
    }
}
