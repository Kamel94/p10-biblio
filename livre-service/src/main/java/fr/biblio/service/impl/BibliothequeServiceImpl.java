package fr.biblio.service.impl;

import fr.biblio.dao.BibliothequeRepository;
import fr.biblio.entities.Bibliotheque;
import fr.biblio.service.contract.BibliothequeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BibliothequeServiceImpl implements BibliothequeService {

    @Autowired
    private BibliothequeRepository repository;

    @Override
    public List<Bibliotheque> findAll() {
        return repository.findAll();
    }

    @Override
    public Bibliotheque findById(long id) {
        return repository.findById(id).get();
    }

    @Override
    public Bibliotheque save(Bibliotheque bibliotheque) {
        return repository.save(bibliotheque);
    }
}
