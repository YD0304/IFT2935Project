package com.ift2935.service;

import com.ift2935.dao.UtilisateurDAO;
import com.ift2935.model.Utilisateur;

public class AuthService {

    public static Utilisateur loginByEmail(String email) {
        try {
            UtilisateurDAO dao = new UtilisateurDAO();
            return dao.findByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    public static Utilisateur login(String email, String password) {
        String sql = "SELECT id_user, nom, prenom, email, telephone, adresse, date_inscription, type_utilisateur " +
        "FROM utilisateur WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur user = new Utilisateur(
                        rs.getInt("id_user"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse"),
                        rs.getDate("date_inscription").toLocalDate(), // if LocalDate
                        rs.getString("type_utilisateur")
                    );
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    } */
}