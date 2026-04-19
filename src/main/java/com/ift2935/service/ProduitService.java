package com.ift2935.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ift2935.dao.ProduitDAO;
import com.ift2935.model.Produit;


public class ProduitService {

    private final ProduitDAO produitDAO = new ProduitDAO();

    /**
     * Submits a new product to the system (by an announcer).
     * Validates required fields before persisting.
     */
    public Produit soumettreProduit(int idAnnonceur, int idCategorie, String titre,
                                    String description, String marque, String modele,
                                    String etatProduit, BigDecimal prix_souhaite) throws Exception {
        if (titre == null || titre.isBlank())
            throw new IllegalArgumentException("Le titre du produit est obligatoire.");
        if (prix_souhaite == null || prix_souhaite.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Le prix souhaité doit être positif.");

        Produit produit = new Produit(
            0, idAnnonceur, idCategorie, titre, description,
            marque, modele, etatProduit, prix_souhaite, new Date()
        );
        int id = produitDAO.insert(produit);
        produit.setIdProduit(id);
        return produit;
    }

    /**
     * Retrieves a product by its ID.
     */
    public Produit getProduitById(int idProduit) throws Exception {
        Produit p = produitDAO.findById(idProduit);
        if (p == null)
            throw new IllegalArgumentException("Produit introuvable (id=" + idProduit + ").");
        return p;
    }

    /**
     * Returns all products submitted by a given announcer.
     */
    public List<Produit> getProduitsByAnnonceur(int idAnnonceur) throws Exception {
        return produitDAO.findByAnnonceur(idAnnonceur);
    }

    /**
     * Returns all products that have not yet received an expert estimation.
     * (Used by experts to find products to evaluate.)
     */
    public List<Produit> getProduitsSansEstimation() throws Exception {
        return produitDAO.findWithoutEstimation();
    }

    /**
     * Updates the mutable fields of an existing product.
     * Only the announcer who owns the product may update it.
     */
    public void updateProduit(int idProduit, int idAnnonceur, int idCategorie,
                              String titre, String description, String marque,
                              String modele, String etatProduit,
                              BigDecimal prix_souhaite) throws Exception {
        Produit existing = getProduitById(idProduit);
        if (existing.getIdAnnonceur() != idAnnonceur)
            throw new SecurityException("Vous n'êtes pas autorisé à modifier ce produit.");

        existing.setIdCategorie(idCategorie);
        existing.setTitre(titre);
        existing.setDescription(description);
        existing.setMarque(marque);
        existing.setModele(modele);
        existing.setEtatProduit(etatProduit);
        existing.setPrix_souhaite(prix_souhaite);
        produitDAO.update(existing);
    }
}
