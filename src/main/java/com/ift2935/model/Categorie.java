package com.ift2935.model;

public class Categorie {
    int id_categorie;
    String nom;
    String description;

    public Categorie(int id_categorie, String nom, String description) {
        this.id_categorie = id_categorie;
        this.nom = nom;
        this.description = description;
    }

    public int getIdCategorie() { return id_categorie; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }

    public void setIdCategorie(int id_categorie) { this.id_categorie = id_categorie; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDescription(String description) { this.description = description; }

}
