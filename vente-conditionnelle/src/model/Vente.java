package com.ift2935.model;
import java.math.BigDecimal;
import java.util.Date;

public class Vente {
    private int id_vente;
    private int id_offre;
    private BigDecimal prix_final;
    private Date date_vente;
    private String mode_conclusion;

    public Vente(int id_vente, int id_offre, BigDecimal prix_final, Date date_vente, String mode_conclusion) {
        this.id_vente = id_vente;
        this.id_offre = id_offre;
        this.prix_final = prix_final;
        this.date_vente = date_vente;
        this.mode_conclusion = mode_conclusion;
    }

    public int getId_vente() {
        return id_vente;
    }
    public int getId_offre() {
        return id_offre;
    }
    public BigDecimal getPrix_final() {
        return prix_final;
    }
    public Date getDate_vente() {
        return date_vente;
    }
    public String getMode_conclusion() {
        return mode_conclusion;
    }
    public void setId_vente(int id_vente) {
        this.id_vente = id_vente;
    }
    public void setId_offre(int id_offre) {
        this.id_offre = id_offre;
    }
    public void setPrix_final(BigDecimal prix_final) {
        this.prix_final = prix_final;
    }
    public void setDate_vente(Date date_vente) {
        this.date_vente = date_vente;
    }
    public void setMode_conclusion(String mode_conclusion) {
        this.mode_conclusion = mode_conclusion;
    }
}
