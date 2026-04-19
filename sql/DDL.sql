DROP TABLE IF EXISTS Vente CASCADE;
DROP TABLE IF EXISTS Offre CASCADE;
DROP TABLE IF EXISTS Annonce CASCADE;
DROP TABLE IF EXISTS Estimation CASCADE;
DROP TABLE IF EXISTS Produit CASCADE;
DROP TABLE IF EXISTS Categorie CASCADE;
DROP TABLE IF EXISTS Utilisateur CASCADE;

CREATE TABLE Utilisateur (
    id_user SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    date_inscription DATE NOT NULL,
    type_utilisateur VARCHAR(20) NOT NULL,
    CHECK (type_utilisateur IN ('annonceur','acheteur','expert'))
);

CREATE TABLE Categorie (
    id_categorie SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE Produit (
    id_produit SERIAL PRIMARY KEY,
    id_annonceur INT NOT NULL REFERENCES Utilisateur(id_user),
    id_categorie INT NOT NULL REFERENCES Categorie(id_categorie),
    titre VARCHAR(150) NOT NULL,
    description TEXT,
    marque VARCHAR(100),
    modele VARCHAR(100),
    etat_produit VARCHAR(50) NOT NULL,
    prix_souhaite NUMERIC(10,2) NOT NULL,
    date_soumission DATE NOT NULL,
    CHECK (prix_souhaite > 0)
);

CREATE TABLE Estimation (
    id_estimation SERIAL PRIMARY KEY,
    id_produit INT NOT NULL UNIQUE REFERENCES Produit(id_produit),
    id_expert INT NOT NULL REFERENCES Utilisateur(id_user),
    prix_estime NUMERIC(10,2) NOT NULL,
    commentaire TEXT,
    date_estimation DATE NOT NULL,
    decision_annonceur VARCHAR(20) NOT NULL DEFAULT 'en_attente',
    date_decision DATE,
    CHECK (prix_estime > 0),
    CHECK (decision_annonceur IN ('acceptee','refusee','en_attente')),
    CHECK (date_decision IS NULL OR date_decision >= date_estimation)
);

CREATE TABLE Annonce (
    id_annonce SERIAL PRIMARY KEY,
    id_produit INT NOT NULL UNIQUE REFERENCES Produit(id_produit),
    date_publication DATE NOT NULL,
    date_expiration DATE,
    statut_annonce VARCHAR(20) NOT NULL,
    CHECK (statut_annonce IN ('active','vendue','retiree','expiree')),
    CHECK (date_expiration IS NULL OR date_expiration >= date_publication)
);

CREATE TABLE Offre (
    id_offre SERIAL PRIMARY KEY,
    id_annonce INT NOT NULL REFERENCES Annonce(id_annonce),
    id_acheteur INT NOT NULL REFERENCES Utilisateur(id_user),
    montant_offre NUMERIC(10,2) NOT NULL,
    date_offre DATE NOT NULL,
    statut_offre VARCHAR(20) NOT NULL DEFAULT 'en_attente',
    CHECK (montant_offre > 0),
    CHECK (statut_offre IN ('en_attente','acceptee','refusee'))
);

CREATE TABLE Vente (
    id_vente SERIAL PRIMARY KEY,
    id_offre INT NOT NULL UNIQUE REFERENCES Offre(id_offre),
    prix_final NUMERIC(10,2) NOT NULL,
    date_vente DATE NOT NULL,
    mode_conclusion VARCHAR(20) NOT NULL,
    CHECK (prix_final > 0),
    CHECK (mode_conclusion IN ('automatique','manuelle'))
);
