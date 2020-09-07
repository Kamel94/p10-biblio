package fr.biblio.service.contract;

import fr.biblio.entities.Bibliotheque;

import java.util.List;

public interface BibliothequeService {

    List<Bibliotheque> findAll();
    Bibliotheque findById(long id);
    Bibliotheque save(Bibliotheque bibliotheque);
}
