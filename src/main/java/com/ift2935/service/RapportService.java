package com.ift2935.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ift2935.DBConnection;

/**
 * Service that executes the 10 complex SQL queries required by the project.
 * Each method returns a list of rows, each row represented as a Map<String, Object>
 * where the key is the column name (or alias) and the value is the column value.
 */
public class RapportService {

    // Helper method to convert ResultSet to List<Map<String, Object>>
    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(meta.getColumnLabel(i), rs.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }

    /**
     * Requête 1 : Pour chaque catégorie, écart moyen entre l'estimation expert et le prix de vente final,
     * pour les produits vendus dont l'estimation a été acceptée.
     * Jointures : Categorie, Produit, Estimation, Annonce, Offre, Vente (6 tables)
     */
    public List<Map<String, Object>> getEcartMoyenParCategorie() throws SQLException {
        String sql = 
            "SELECT c.nom AS categorie, " +
            "       ROUND(AVG(e.prix_estime - v.prix_final), 2) AS ecart_moyen, " +
            "       COUNT(DISTINCT p.id_produit) AS nb_ventes " +
            "FROM Categorie c " +
            "JOIN Produit p    ON c.id_categorie = p.id_categorie " +
            "JOIN Estimation e ON p.id_produit = e.id_produit " +
            "JOIN Annonce a    ON p.id_produit = a.id_produit " +
            "JOIN Offre o      ON a.id_annonce = o.id_annonce " +
            "JOIN Vente v      ON o.id_offre = v.id_offre " +
            "WHERE a.statut_annonce = 'vendue' " +
            "  AND e.decision_annonceur = 'acceptee' " +
            "GROUP BY c.id_categorie, c.nom " +
            "ORDER BY ecart_moyen DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    /**
     * Requête 2 : Acheteurs ayant réalisé le plus d'achats automatiques, avec montant total dépensé.
     * Jointures : Utilisateur, Offre, Vente (3 tables)
     */
    public List<Map<String, Object>> getTopAcheteursAutomatiques() throws SQLException {
        String sql = 
            "SELECT u.id_user, u.prenom, u.nom, " +
            "       COUNT(v.id_vente) AS nb_achats, " +
            "       SUM(v.prix_final) AS total_depense " +
            "FROM Utilisateur u " +
            "JOIN Offre o ON u.id_user = o.id_acheteur " +
            "JOIN Vente v ON o.id_offre = v.id_offre " +
            "WHERE u.type_utilisateur = 'acheteur' " +
            "  AND v.mode_conclusion = 'automatique' " +
            "GROUP BY u.id_user, u.prenom, u.nom " +
            "ORDER BY total_depense DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    /**
     * Requête 3 : Produits avec estimation acceptée mais jamais annoncés (aucune annonce).
     * Jointures : Produit, Estimation, Utilisateur (expert), Annonce (LEFT JOIN) – 4 tables.
     */
    public List<Map<String, Object>> getProduitsEstimesSansAnnonce() throws SQLException {
        String sql = 
            "SELECT p.id_produit, p.titre, p.prix_souhaite, " +
            "       e.prix_estime, e.commentaire, " +
            "       u.prenom AS expert_prenom, u.nom AS expert_nom, " +
            "       e.date_estimation " +
            "FROM Produit p " +
            "JOIN Estimation e   ON p.id_produit = e.id_produit " +
            "JOIN Utilisateur u  ON e.id_expert = u.id_user " +
            "LEFT JOIN Annonce a ON p.id_produit = a.id_produit " +
            "WHERE e.decision_annonceur = 'acceptee' " +
            "  AND u.type_utilisateur = 'expert' " +
            "  AND a.id_annonce IS NULL " +
            "ORDER BY e.date_estimation DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    /**
     * Requête 4 : Annonces terminées (expirées ou retirées) sans offre acceptée,
     * avec nombre d'offres reçues et offre maximale.
     * Jointures : Annonce, Produit, Utilisateur, Offre – 4 tables.
     */
    public List<Map<String, Object>> getAnnoncesTermineesSansVente() throws SQLException {
        String sql = 
            "SELECT a.id_annonce, p.titre, p.prix_souhaite, " +
            "       u.prenom AS annonceur_prenom, u.nom AS annonceur_nom, " +
            "       u.email AS annonceur_email, " +
            "       COUNT(o.id_offre) AS nb_offres, " +
            "       MAX(o.montant_offre) AS offre_max, " +
            "       a.statut_annonce " +
            "FROM Annonce a " +
            "JOIN Produit p     ON a.id_produit = p.id_produit " +
            "JOIN Utilisateur u ON p.id_annonceur = u.id_user " +
            "JOIN Offre o       ON a.id_annonce = o.id_annonce " +
            "WHERE a.statut_annonce IN ('expiree', 'retiree') " +
            "GROUP BY a.id_annonce, p.titre, p.prix_souhaite, " +
            "         u.prenom, u.nom, u.email, a.statut_annonce " +
            "HAVING SUM(CASE WHEN o.statut_offre = 'acceptee' THEN 1 ELSE 0 END) = 0 " +
            "ORDER BY nb_offres DESC, offre_max DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    /**
     * Requête 5 : Répartition des ventes par tranche de prix et mode de conclusion.
     * Table seule : Vente (pas de jointure, mais requête analytique valide).
     */
    public List<Map<String, Object>> getVentesParTranchePrix() throws SQLException {
        String sql = 
            "SELECT " +
            "  CASE " +
            "    WHEN prix_final < 100  THEN '1_moins_100' " +
            "    WHEN prix_final < 500  THEN '2_de_100_a_500' " +
            "    WHEN prix_final < 1000 THEN '3_de_500_a_1000' " +
            "    WHEN prix_final < 5000 THEN '4_de_1000_a_5000' " +
            "    ELSE '5_plus_5000' " +
            "  END AS tranche_prix, " +
            "  mode_conclusion, " +
            "  COUNT(*) AS nb_ventes, " +
            "  ROUND(AVG(prix_final), 2) AS prix_moyen, " +
            "  SUM(prix_final) AS total_ventes " +
            "FROM Vente " +
            "GROUP BY tranche_prix, mode_conclusion " +
            "ORDER BY tranche_prix, mode_conclusion";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    /**
     * Requête 6 : Produits dont le prix souhaité est >20% au-dessus de la moyenne des estimations
     * de leur catégorie (pour les produits ayant une annonce active ou vendue).
     * Jointures : CTE + Produit, Categorie, Estimation, Annonce – 4 tables.
     */
    public List<Map<String, Object>> getProduitsSurprix() throws SQLException {
        String sql = 
            "WITH avg_par_cat AS ( " +
            "  SELECT p.id_categorie, AVG(e.prix_estime) AS avg_estime " +
            "  FROM Produit p " +
            "  JOIN Estimation e ON p.id_produit = e.id_produit " +
            "  WHERE e.decision_annonceur != 'refusee' " +
            "  GROUP BY p.id_categorie " +
            ") " +
            "SELECT c.nom AS categorie, " +
            "       p.titre, " +
            "       p.prix_souhaite, " +
            "       e.prix_estime, " +
            "       ROUND(ac.avg_estime, 2) AS moyenne_categorie, " +
            "       ROUND((p.prix_souhaite / ac.avg_estime - 1) * 100, 1) AS ecart_pct " +
            "FROM Produit p " +
            "JOIN Categorie c     ON p.id_categorie = c.id_categorie " +
            "JOIN Estimation e    ON p.id_produit = e.id_produit " +
            "JOIN Annonce a       ON p.id_produit = a.id_produit " +
            "JOIN avg_par_cat ac  ON p.id_categorie = ac.id_categorie " +
            "WHERE e.decision_annonceur != 'refusee' " +
            "  AND a.statut_annonce IN ('active', 'vendue') " +
            "  AND p.prix_souhaite > ac.avg_estime * 1.20 " +
            "ORDER BY ecart_pct DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    /**
     * Requête 7 : Experts ayant réalisé au moins 5 estimations, avec leur taux d'acceptation.
     * Jointures : Utilisateur, Estimation – 2 tables.
     */
    public List<Map<String, Object>> getPerformanceExperts() throws SQLException {
        String sql = 
            "SELECT u.id_user AS expert_id, " +
            "       u.prenom || ' ' || u.nom AS expert_nom, " +
            "       COUNT(e.id_estimation) AS total_estimations, " +
            "       SUM(CASE WHEN e.decision_annonceur = 'acceptee' THEN 1 ELSE 0 END) AS acceptees, " +
            "       ROUND(100.0 * SUM(CASE WHEN e.decision_annonceur = 'acceptee' THEN 1 ELSE 0 END) " +
            "             / COUNT(e.id_estimation), 1) AS taux_acceptation_pct " +
            "FROM Utilisateur u " +
            "JOIN Estimation e ON u.id_user = e.id_expert " +
            "WHERE u.type_utilisateur = 'expert' " +
            "GROUP BY u.id_user, u.prenom, u.nom " +
            "HAVING COUNT(e.id_estimation) >= 5 " +
            "ORDER BY taux_acceptation_pct DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    /**
     * Requête 8 : Pour chaque catégorie, nombre de produits, prix moyen/min/max souhaité.
     * Jointures : Categorie, Produit – 2 tables.
     */
    public List<Map<String, Object>> getStatistiquesPrixParCategorie() throws SQLException {
        String sql = 
            "SELECT c.nom AS categorie, " +
            "       COUNT(p.id_produit) AS nb_produits, " +
            "       ROUND(AVG(p.prix_souhaite), 2) AS prix_moyen_souhaite, " +
            "       MIN(p.prix_souhaite) AS prix_min, " +
            "       MAX(p.prix_souhaite) AS prix_max " +
            "FROM Categorie c " +
            "JOIN Produit p ON c.id_categorie = p.id_categorie " +
            "GROUP BY c.id_categorie, c.nom " +
            "ORDER BY nb_produits DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    /**
     * Requête 9 : Pour les ventes manuelles, rapport entre le prix final et l'offre maximale reçue.
     * Jointures : CTE + Annonce, Produit, Offre, Vente – 5 tables.
     */
    public List<Map<String, Object>> getRapportPrixFinalOffreMax() throws SQLException {
        String sql = 
            "WITH offre_max_par_annonce AS ( " +
            "  SELECT id_annonce, MAX(montant_offre) AS max_offre " +
            "  FROM Offre " +
            "  GROUP BY id_annonce " +
            ") " +
            "SELECT a.id_annonce, " +
            "       p.titre, " +
            "       v.prix_final, " +
            "       om.max_offre, " +
            "       ROUND(100.0 * v.prix_final / om.max_offre, 1) AS rapport_pct " +
            "FROM Annonce a " +
            "JOIN Produit p ON a.id_produit = p.id_produit " +
            "JOIN Offre o ON a.id_annonce = o.id_annonce " +
            "JOIN Vente v ON o.id_offre = v.id_offre " +
            "JOIN offre_max_par_annonce om ON a.id_annonce = om.id_annonce " +
            "WHERE v.mode_conclusion = 'manuelle' " +
            "ORDER BY rapport_pct ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }

    /**
     * Requête 10 : Taux de publication des produits par catégorie (pourcentage de produits qui ont une annonce).
     * Jointures : Categorie, Produit, Annonce (LEFT JOIN) – 3 tables.
     */
    public List<Map<String, Object>> getTauxPublicationParCategorie() throws SQLException {
        String sql = 
            "SELECT c.nom AS categorie, " +
            "       COUNT(DISTINCT p.id_produit) AS nb_produits_total, " +
            "       COUNT(DISTINCT a.id_produit) AS nb_produits_annonces, " +
            "       ROUND(100.0 * COUNT(DISTINCT a.id_produit) / COUNT(DISTINCT p.id_produit), 1) " +
            "         AS taux_publication_pct " +
            "FROM Categorie c " +
            "JOIN Produit p      ON c.id_categorie = p.id_categorie " +
            "LEFT JOIN Annonce a ON p.id_produit = a.id_produit " +
            "GROUP BY c.id_categorie, c.nom " +
            "ORDER BY taux_publication_pct DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return resultSetToList(rs);
        }
    }
}