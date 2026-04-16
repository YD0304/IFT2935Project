package com.ift2935.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ift2935.DBConnection;
import com.ift2935.model.Vente;

public class VenteDAO {

    public int insert(Vente vente) throws SQLException {
        String sql = "INSERT INTO Vente (id_offre, prix_final, date_vente, mode_conclusion) VALUES (?,?,?,?) RETURNING id_vente";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vente.getId_offre());
            stmt.setBigDecimal(2, vente.getPrix_final());
            stmt.setTimestamp(3, new Timestamp(vente.getDate_vente().getTime()));
            stmt.setString(4, vente.getMode_conclusion());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Insert failed");
        }
    }

    // Transaction-aware version
    public int insert(Connection conn, Vente vente) throws SQLException {
        String sql = "INSERT INTO Vente (id_offre, prix_final, date_vente, mode_conclusion) VALUES (?,?,?,?) RETURNING id_vente";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vente.getId_offre());
            stmt.setBigDecimal(2, vente.getPrix_final());
            stmt.setTimestamp(3, new Timestamp(vente.getDate_vente().getTime()));
            stmt.setString(4, vente.getMode_conclusion());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Insert failed");
        }
    }
}