package fr.biblio.service.impl;

import fr.biblio.dao.ExemplaireLivreRepository;
import fr.biblio.entities.ExemplaireLivre;
import fr.biblio.service.contract.ExemplaireLivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExemplaireLivreServiceImpl implements ExemplaireLivreService {

    @Autowired
    private ExemplaireLivreRepository repository;

    @Override
    public List<ExemplaireLivre> findAll() {
        return repository.findAll();
    }

    @Override
    public List<ExemplaireLivre> findByLivreId(long id) {
        return repository.findByLivreId(id);
    }

    @Override
    public ExemplaireLivre findById(long id) {
        return repository.findById(id).get();
    }

    @Override
    public ExemplaireLivre findExemplaireLivreByLivreIdAndBibliothequeId(long livreId, long bibliothequeId) {
        return repository.findExemplaireLivreByLivreIdAndBibliothequeId(livreId, bibliothequeId);
    }

    @Override
    public ExemplaireLivre findExemplaireLivresByLivreId(long livreId) {
        return repository.findExemplaireLivresByLivreId(livreId);
    }

    @Override
    public ExemplaireLivre save(ExemplaireLivre exemplaireLivre) {
        return repository.save(exemplaireLivre);
    }
}
