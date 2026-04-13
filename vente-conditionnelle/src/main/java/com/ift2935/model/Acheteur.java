package com.ift2935.model;

public class Acheteur extends Utilisateur {
    public Acheteur(int id, String nom, String prenom, String email, String motDePasse) {
        super(id, nom, prenom, email, motDePasse, "acheteur");
    }
}