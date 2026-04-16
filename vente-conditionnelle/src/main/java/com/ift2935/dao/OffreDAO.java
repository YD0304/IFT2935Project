package com.ift2935.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ift2935.DBConnection;
import com.ift2935.model.Offre;

public class OffreDAO {
    public int insert(Offre offre) throws SQLException {
        String sql = "INSERT INTO Offre (id_annonce, id_acheteur, montant_offre, date_offre, statut_offre) VALUES (?,?,?,?,?) RETURNING id_offre";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offre.getId_annonce());
            stmt.setInt(2, offre.getId_acheteur());
            stmt.setBigDecimal(3, offre.getMontant_offre());
            stmt.setTimestamp(4, new Timestamp(offre.getDate_offre().getTime()));
            stmt.setString(5, offre.getStatut_offre());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Insert failed");
        }
    }

    // Transaction-aware version
    public int insert(Connection conn, Offre offre) throws SQLException {
        String sql = "INSERT INTO Offre (id_annonce, id_acheteur, montant_offre, date_offre, statut_offre) VALUES (?,?,?,?,?) RETURNING id_offre";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, offre.getId_annonce());
            stmt.setInt(2, offre.getId_acheteur());
            stmt.setBigDecimal(3, offre.getMontant_offre());
            stmt.setTimestamp(4, new Timestamp(offre.getDate_offre().getTime()));
            stmt.setString(5, offre.getStatut_offre());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Insert failed");
        }
    }
}