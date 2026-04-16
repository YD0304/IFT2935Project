package com.ift2935.model;

import java.time.LocalDate;

public class Utilisateur {

    private int id_utilisateur;
    private String nom;
    private String prenom;
    private String couriel;
    private String telephone;
    private String adresse;
    private LocalDate date_incription;
    private String role; // "acheteur", "annonceur", "expert"



    public Utilisateur(int id_utilisateur, String nom, String prenom, String couriel, String telephone, String adresse, LocalDate date_incription, String role) {
        this.id_utilisateur = id_utilisateur;
        this.nom = nom;
        this.prenom = prenom;
        this.couriel = couriel;
        this.telephone = telephone;
        this.adresse = adresse;
        this.date_incription = date_incription;
        this.role = role;
    }

    // Getters and setters...
    public int getId() { return id_utilisateur; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return couriel; }
    public String getTelephone() { return telephone; }
    public String getAdresse() { return adresse; }
    public LocalDate getDateInscription() { return date_incription; }
    public String getRole() { return role; }

    public void setId(int id_utilisateur) { this.id_utilisateur = id_utilisateur; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String couriel) { this.couriel = couriel; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setDateInscription(LocalDate date_incription) { this.date_incription = date_incription; }
    public void setRole(String role) { this.role = role; }
}