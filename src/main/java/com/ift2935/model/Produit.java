package com.ift2935.model;

import java.math.BigDecimal;
import java.util.Date;

public class Produit {
    private int id_produit;
    private int id_annonceur;
    private int id_categorie;
    private String titre;
    private String description;
    private String marque;
    private String modele;
    private String etat_produit;
    private BigDecimal prix_souhaite;
    private Date date_soumission;   // renamed from dateSoumission

    public Produit(int id_produit, int id_annonceur, int id_categorie, String titre,
                   String description, String marque, String modele, String etat_produit,
                   BigDecimal prix_souhaite, Date date_soumission) {
        this.id_produit = id_produit;
        this.id_annonceur = id_annonceur;
        this.id_categorie = id_categorie;
        this.titre = titre;
        this.description = description;
        this.marque = marque;
        this.modele = modele;
        this.etat_produit = etat_produit;
        this.prix_souhaite = prix_souhaite;
        this.date_soumission = date_soumission;
    }

    // Getters and setters
    public int getIdProduit() { return id_produit; }
    public void setIdProduit(int id_produit) { this.id_produit = id_produit; }

    public int getIdAnnonceur() { return id_annonceur; }
    public void setIdAnnonceur(int id_annonceur) { this.id_annonceur = id_annonceur; }

    public int getIdCategorie() { return id_categorie; }
    public void setIdCategorie(int id_categorie) { this.id_categorie = id_categorie; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public String getEtatProduit() { return etat_produit; }
    public void setEtatProduit(String etat_produit) { this.etat_produit = etat_produit; }

    public BigDecimal getPrix_souhaite() { return prix_souhaite; }
    public void setPrix_souhaite(BigDecimal prix_souhaite) { this.prix_souhaite = prix_souhaite; }

    public Date getDate_soumission() { return date_soumission; }
    public void setDate_soumission(Date date_soumission) { this.date_soumission = date_soumission; }
}
