package com.ift2935.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ift2935.DBConnection;
import com.ift2935.model.Utilisateur;

public class UtilisateurDAO {

    public Utilisateur findById(int id) throws SQLException {
        String sql = "SELECT id_user, nom, prenom, email, telephone, adresse, date_inscription " +
                     "FROM Utilisateur WHERE id_user = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Utilisateur u = new Utilisateur(
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("telephone"),
                    rs.getString("adresse"),
                    rs.getDate("date_inscription").toLocalDate(),
                    null // Replace with the appropriate String value if needed
                );
                u.setId(rs.getInt("id_user"));
                u.setRole(getRole(id));
                return u;
            }
            return null;
        }
    }

    /* 
    // Role management (merged from UtilisateurRoleDAO)
    public List<String> getRoles(int userId) throws SQLException {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT role FROM Utilisateur_Role WHERE id_user = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) roles.add(rs.getString("role"));
        }
        return roles;
    }

    public void addRole(int userId, String role) throws SQLException {
        String sql = "INSERT INTO Utilisateur_Role (id_user, role) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, role);
            stmt.executeUpdate();
        }
    }

    public boolean hasRole(int userId, String role) throws SQLException {
        String sql = "SELECT 1 FROM Utilisateur_Role WHERE id_user = ? AND role = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, role);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    */

    // Simple getter for single role
public String getRole(int userId) throws SQLException {
    String sql = "SELECT role FROM Utilisateur WHERE id_user = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getString("role") : null;
    }
}

// Update role
public void updateRole(int userId, String role) throws SQLException {
    String sql = "UPDATE Utilisateur SET role = ? WHERE id_user = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, role);
        stmt.setInt(2, userId);
        stmt.executeUpdate();
    }
}
    // Basic CRUD
    public void insert(Utilisateur user) throws SQLException {
        String sql = "INSERT INTO Utilisateur (nom, prenom, email, telephone, adresse, date_inscription) VALUES (?,?,?,?,?,?) RETURNING id_user";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getNom());
            stmt.setString(2, user.getPrenom());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getTelephone());
            stmt.setString(5, user.getAdresse());
            stmt.setTimestamp(6, Timestamp.valueOf(user.getDateInscription().atStartOfDay()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) user.setId(rs.getInt(1));
        }
    }
}