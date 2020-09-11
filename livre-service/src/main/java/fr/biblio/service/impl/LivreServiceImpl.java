package fr.biblio.service.impl;

import fr.biblio.dao.LivreRepository;
import fr.biblio.entities.Livre;
import fr.biblio.service.contract.LivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivreServiceImpl implements LivreService {

    @Autowired
    private LivreRepository repository;

    @Override
    public List<Livre> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Livre> recherche(String titre, String auteur, String categorie) {
        return repository.recherche("%" + titre + "%", "%" + auteur + "%",  "%" + categorie + "%");
    }

    @Override
    public Livre findById(long id) {
        return repository.findById(id).get();
    }

    @Override
    public Livre save(Livre livre) {
        return repository.save(livre);
    }
}
