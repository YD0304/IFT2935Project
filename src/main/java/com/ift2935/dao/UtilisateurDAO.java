package com.ift2935.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ift2935.DBConnection;
import com.ift2935.model.Utilisateur;

public class UtilisateurDAO {
    public Utilisateur findById(int id) throws SQLException {
        String sql = "SELECT id_user, nom, prenom, email, telephone, adresse, date_inscription, type_utilisateur " +
                     "FROM Utilisateur WHERE id_user = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("telephone"),
                    rs.getString("adresse"),
                    rs.getDate("date_inscription").toLocalDate(),
                    rs.getString("type_utilisateur")   // directly from query
                );
            }
            return null;
        }
    }


    public Utilisateur findByEmail(String email) throws SQLException {
        String sql = "SELECT id_user, nom, prenom, email, telephone, adresse, date_inscription, type_utilisateur " +
                     "FROM Utilisateur WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("telephone"),
                    rs.getString("adresse"),
                    rs.getDate("date_inscription").toLocalDate(),
                    rs.getString("type_utilisateur")
                );
            }
            return null;
        }
    }

    public void insert(Utilisateur user) throws SQLException {
            String sql = "INSERT INTO Utilisateur (nom, prenom, email, telephone, adresse, date_inscription, type_utilisateur) " +
                         "VALUES (?,?,?,?,?,?,?) RETURNING id_user";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getNom());
                stmt.setString(2, user.getPrenom());
                stmt.setString(3, user.getEmail());
                stmt.setString(4, user.getTelephone());
                stmt.setString(5, user.getAdresse());
                stmt.setDate(6, java.sql.Date.valueOf(user.getDateInscription()));
                stmt.setString(7, user.gettype_utilisateur());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) user.setId(rs.getInt(1));
            }
        }

        public void update(Utilisateur user) throws SQLException {
            String sql = "UPDATE Utilisateur SET nom=?, prenom=?, email=?, telephone=?, adresse=?, date_inscription=?, type_utilisateur=? WHERE id_user=?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getNom());
                stmt.setString(2, user.getPrenom());
                stmt.setString(3, user.getEmail());
                stmt.setString(4, user.getTelephone());
                stmt.setString(5, user.getAdresse());
                stmt.setDate(6, java.sql.Date.valueOf(user.getDateInscription()));
                stmt.setString(7, user.gettype_utilisateur());
                stmt.setInt(8, user.getId());
                stmt.executeUpdate();
            }
        }

}
