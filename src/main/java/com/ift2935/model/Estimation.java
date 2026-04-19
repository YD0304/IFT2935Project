package com.ift2935.model;

import java.math.BigDecimal;
import java.util.Date;

public class Estimation {
    private int id_estimation;
    private int id_produit;
    private int id_expert;
    private BigDecimal prix_estime;
    private String commentaire;
    private Date date_estimation;
    private String decision_annonceur;      // 'en_attente', 'acceptee', 'refusee'
    private Date date_decision;

    // Full constructor (8 parameters) – used by DAO's map()
    public Estimation(int id_estimation, int id_produit, int id_expert, BigDecimal prix_estime,
                      String commentaire, Date date_estimation, String decision_annonceur, Date date_decision) {
        this.id_estimation = id_estimation;
        this.id_produit = id_produit;
        this.id_expert = id_expert;
        this.prix_estime = prix_estime;
        this.commentaire = commentaire;
        this.date_estimation = date_estimation;
        this.decision_annonceur = decision_annonceur;
        this.date_decision = date_decision;
    }

    // Convenience constructor without decision/date_decision (for new estimations)
    public Estimation(int id_produit, int id_expert, BigDecimal prix_estime, String commentaire, Date date_estimation) {
        this(0, id_produit, id_expert, prix_estime, commentaire, date_estimation, "en_attente", null);
    }

    // Getters and setters (all fields)
    public int getId_estimation() { return id_estimation; }
    public void setId_estimation(int id_estimation) { this.id_estimation = id_estimation; }

    public int getId_produit() { return id_produit; }
    public void setId_produit(int id_produit) { this.id_produit = id_produit; }

    public int getId_expert() { return id_expert; }
    public void setId_expert(int id_expert) { this.id_expert = id_expert; }

    public BigDecimal getPrix_estime() { return prix_estime; }
    public void setPrix_estime(BigDecimal prix_estime) { this.prix_estime = prix_estime; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Date getDate_estimation() { return date_estimation; }
    public void setDate_estimation(Date date_estimation) { this.date_estimation = date_estimation; }

    public String getDecision_annonceur() { return decision_annonceur; }
    public void setDecision_annonceur(String decision_annonceur) { this.decision_annonceur = decision_annonceur; }

    public Date getDateDecision() { return date_decision; }
    public void setDateDecision(Date date_decision) { this.date_decision = date_decision; }
}
