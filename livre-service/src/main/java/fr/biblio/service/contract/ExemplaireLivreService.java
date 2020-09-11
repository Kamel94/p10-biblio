package fr.biblio.service.contract;

import fr.biblio.entities.ExemplaireLivre;

import java.util.List;

public interface ExemplaireLivreService {

    List<ExemplaireLivre> findAll();
    List<ExemplaireLivre> findByLivreId(long id);
    ExemplaireLivre findById(long id);
    ExemplaireLivre findExemplaireLivreByLivreIdAndBibliothequeId(long livreId, long bibliothequeId);
    ExemplaireLivre findExemplaireLivresByLivreId(long livreId);
    ExemplaireLivre save(ExemplaireLivre exemplaireLivre);
}
