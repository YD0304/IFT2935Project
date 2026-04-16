package com.ift2935.model;

import java.math.BigDecimal;
import java.util.Date;

public class Offre {

    private int id_offre;
    private int id_annonce;
    private int id_acheteur;
    private BigDecimal montant_offre;
    private Date date_offre;
    private String statut_offre;

    public Offre(int id_offre, int id_annonce, int id_acheteur, BigDecimal montant_offre, Date date_offre, String statut_offre) {
        this.id_offre = id_offre;
        this.id_annonce = id_annonce;
        this.id_acheteur = id_acheteur;
        this.montant_offre = montant_offre;
        this.date_offre = date_offre;
        this.statut_offre = statut_offre;
    }

    public int getId_offre() {
        return id_offre;
    }
    public int getId_annonce() {
        return id_annonce;
    }
    public int getId_acheteur() {
        return id_acheteur;
    }
    public BigDecimal getMontant_offre() {
        return montant_offre;
    }
    public Date getDate_offre() {
        return date_offre;
    }
    public String getStatut_offre() {
        return statut_offre;
    }

    public void setId_offre(int id_offre) {
        this.id_offre = id_offre;
    }
    public void setId_annonce(int id_annonce) {
        this.id_annonce = id_annonce;
    }
    public void setId_acheteur(int id_acheteur) {
        this.id_acheteur = id_acheteur;
    }
    public void setMontant_offre(BigDecimal montant_offre) {
        this.montant_offre = montant_offre;
    }
    public void setDate_offre(Date date_offre) {
        this.date_offre = date_offre;
    }
    public void setStatut_offre(String statut_offre) {
        this.statut_offre = statut_offre;
    }


    
}
