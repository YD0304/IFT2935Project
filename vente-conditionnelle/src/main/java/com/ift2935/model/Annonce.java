package com.ift2935.model;
import java.util.Date;

public class Annonce {
    private int id_annonce;
    private int id_produit;
    private Date date_publication;
    private Date date_expiration;
    private String statut_annonce;

    public Annonce(int id_annonce, int id_produit, Date date_publication, Date date_expiration, String statut_annonce) {
        this.id_annonce = id_annonce;
        this.id_produit = id_produit;
        this.date_publication = date_publication;
        this.date_expiration = date_expiration;
        this.statut_annonce = statut_annonce;
    }

    public int getId_annonce() {
        return id_annonce;
    }
    public int getId_produit() {
        return id_produit;
    }
    public Date getDate_publication() {
        return date_publication;
    }
    public Date getDate_expiration() {
        return date_expiration;
    }
    public String getStatut_annonce() {
        return statut_annonce;
    }

    public void setId_annonce(int id_annonce) {
        this.id_annonce = id_annonce;
    }
    public void setId_produit(int id_produit) {
        this.id_produit = id_produit;
    }
    public void setDate_publication(Date date_publication) {
        this.date_publication = date_publication;
    }
    public void setDate_expiration(Date date_expiration) {
        this.date_expiration = date_expiration;
    }
    public void setStatut_annonce(String statut_annonce) {
        this.statut_annonce = statut_annonce;
    }

    
}
