package com.ift2935.model;

import java.time.LocalDate;

public class Utilisateur {

    private int id_user;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDate date_inscription;
    private String type_utilisateur; // "acheteur", "annonceur", "expert"



    public Utilisateur(int id_user, String nom, String prenom, String email, String telephone, String adresse, LocalDate date_inscription, String type_utilisateur) {
        this.id_user = id_user;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.date_inscription = date_inscription;
        this.type_utilisateur = type_utilisateur;
    }

    // Getters and setters...
    public int getId() { return id_user; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getAdresse() { return adresse; }
    public LocalDate getDateInscription() { return date_inscription; }
    public String getType_utilisateur() { return type_utilisateur; }

    public void setId(int id_user) { this.id_user = id_user; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setDateInscription(LocalDate date_inscription) { this.date_inscription = date_inscription; }
    public void setType_utilisateur(String type_utilisateur) { this.type_utilisateur = type_utilisateur; }
}
