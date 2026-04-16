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
    private BigDecimal prixSouhaite;
    private Date dateSoumission;
    

    public Produit(int id_produit, int id_annonceur, int id_categorie, String titre, String description, String marque, String modele, String etat_produit, BigDecimal prixSouhaite, Date dateSoumission) {
        this.id_produit = id_produit;
        this.id_annonceur = id_annonceur;
        this.id_categorie = id_categorie;
        this.titre = titre;
        this.description = description;
        this.marque = marque;
        this.modele = modele;
        this.etat_produit = etat_produit;
        this.prixSouhaite = prixSouhaite;
        this.dateSoumission = dateSoumission;
    }

    // Getters and setters...
    public int getIdProduit() { return id_produit; }
    public void setIdProduit(int id_produit) { this.id_produit = id_produit; }
    public int getIdAnnonceur() { return id_annonceur; }
    public int getIdCategorie() { return id_categorie; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getMarque() { return marque; }
    public String getModele() { return modele; }
    public String getEtatProduit() { return etat_produit; }
    public BigDecimal getPrixSouhaite() { return prixSouhaite; }
    public Date getDateSoumission() { return dateSoumission; }

    public void setIdAnnonceur(int id_annonceur) { this.id_annonceur = id_annonceur; }
    public void setIdCategorie(int id_categorie) { this.id_categorie = id_categorie; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setMarque(String marque) { this.marque = marque; }
    public void setModele(String modele) { this.modele = modele; }
    public void setEtatProduit(String etat_produit) { this.etat_produit = etat_produit; }
    public void setPrixSouhaite(BigDecimal prixSouhaite) { this.prixSouhaite = prixSouhaite; }
    public void setDateSoumission(Date dateSoumission) { this.dateSoumission = dateSoumission; }
}