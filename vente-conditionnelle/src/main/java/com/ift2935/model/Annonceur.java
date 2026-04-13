package com.ift2935.model;

public class Annonceur extends Utilisateur {
    public Annonceur(int id, String nom, String prenom, String email, String motDePasse) {
        super(id, nom, prenom, email, motDePasse, "annonceur");
    }
}