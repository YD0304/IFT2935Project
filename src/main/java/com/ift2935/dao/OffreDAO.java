package com.ift2935.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ift2935.DBConnection;
import com.ift2935.model.Offre;

public class OffreDAO {
    public Offre findById(int id) throws SQLException {
        String sql = "SELECT id_offre, id_annonce, id_acheteur, montant_offre, date_offre, statut_offre " +
                "FROM Offre WHERE id_offre = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return map(rs);
            return null;
        }
    }

    public List<Offre> findByAnnonce(int annonceId) throws SQLException {
        List<Offre> list = new ArrayList<>();
        String sql = "SELECT id_offre, id_annonce, id_acheteur, montant_offre, date_offre, statut_offre " +
                "FROM Offre WHERE id_annonce = ? ORDER BY montant_offre DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, annonceId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    public List<Offre> findByAcheteur(int acheteurId) throws SQLException {
        List<Offre> list = new ArrayList<>();
        String sql = "SELECT id_offre, id_annonce, id_acheteur, montant_offre, date_offre, statut_offre " +
                "FROM Offre WHERE id_acheteur = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, acheteurId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                list.add(map(rs));
        }
        return list;
    }

    public int insert(Offre offre) throws SQLException {
        String sql = "INSERT INTO Offre (id_annonce, id_acheteur, montant_offre, date_offre, statut_offre) " +
                "VALUES (?,?,?,?,?) RETURNING id_offre";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offre.getId_annonce());
            stmt.setInt(2, offre.getId_acheteur());
            stmt.setBigDecimal(3, offre.getMontant_offre());
            stmt.setTimestamp(4, new Timestamp(offre.getDate_offre().getTime()));
            stmt.setString(5, offre.getStatut_offre());
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt(1);
            throw new SQLException("Insert failed");
        }
    }

    // ========== Update (full) ==========
    public void update(Offre offre) throws SQLException {
        String sql = "UPDATE Offre SET id_annonce=?, id_acheteur=?, montant_offre=?, date_offre=?, statut_offre=? " +
                "WHERE id_offre=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offre.getId_annonce());
            stmt.setInt(2, offre.getId_acheteur());
            stmt.setBigDecimal(3, offre.getMontant_offre());
            stmt.setTimestamp(4, new Timestamp(offre.getDate_offre().getTime()));
            stmt.setString(5, offre.getStatut_offre());
            stmt.setInt(6, offre.getId_offre());
            int rows = stmt.executeUpdate();
            if (rows == 0)
                throw new SQLException("Offre not found for id: " + offre.getId_offre());
        }
    }

    // ========== Delete ==========
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Offre WHERE id_offre = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0)
                throw new SQLException("Offre not found for id: " + id);
        }
    }

    private Offre map(ResultSet rs) throws SQLException {
        return new Offre(
                rs.getInt("id_offre"),
                rs.getInt("id_annonce"),
                rs.getInt("id_acheteur"),
                rs.getBigDecimal("montant_offre"),
                rs.getTimestamp("date_offre"),
                rs.getString("statut_offre"));
    }

}
