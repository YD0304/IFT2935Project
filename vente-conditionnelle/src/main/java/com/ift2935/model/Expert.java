package com.ift2935.model;

public class Expert extends Utilisateur {
    public Expert(int id, String nom, String prenom, String email, String motDePasse) {
        super(id, nom, prenom, email, motDePasse, "expert");
    }
}