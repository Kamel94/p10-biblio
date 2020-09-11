package fr.biblio.service.contract;

import fr.biblio.entities.Livre;

import java.util.List;

public interface LivreService {

    List<Livre> findAll();
    List<Livre> recherche(String titre, String auteur, String categorie);
    Livre findById(long id);
    Livre save(Livre livre);
}
