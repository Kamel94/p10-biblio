package fr.biblio.controller;

import fr.biblio.entities.ExemplaireLivre;
import fr.biblio.service.contract.ExemplaireLivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExemplaireLivreController {

    @Autowired
    private ExemplaireLivreService exemplaireLivreService;

    /**
     * Affiche la liste des exemplaires.
     */
    @GetMapping("/exemplaireLivre")
    public List<ExemplaireLivre> listeDesExemplaires() {
        List<ExemplaireLivre> exemplaireLivres = exemplaireLivreService.findAll();

        return exemplaireLivres;
    }

    /**
     * Affiche un exemplaire par son ID.
     */
    @GetMapping("/exemplaireLivre/{id}")
    public ExemplaireLivre getExemplaire(@PathVariable("id") long id) {

        ExemplaireLivre exemplaireLivre = exemplaireLivreService.findById(id);

        return exemplaireLivre;
    }

    /**
     * Affiche une liste d'exemplaire par l'ID du livre.
     */
    @GetMapping("/listExemplaireLivres/{livreId}")
    public List<ExemplaireLivre> getListExemplairesWithLivreId(@PathVariable("livreId") long id) {

        List<ExemplaireLivre> exemplaireLivre = exemplaireLivreService.findByLivreId(id);

        return exemplaireLivre;
    }

    /**
     * Affiche un exemplaire par l'ID du livre.
     */
    @GetMapping("/exemplaireLivres/{livreId}")
    public ExemplaireLivre getExemplaireWithLivreId(@PathVariable("livreId") long id) {

        ExemplaireLivre exemplaireLivre = exemplaireLivreService.findExemplaireLivresByLivreId(id);

        return exemplaireLivre;
    }

    /**
     * Affiche un exemplaire de par les IDs du livre et de la bibliothèque.
     */
    @GetMapping("/exemplaireLivres/{livreId}/{bibliothequeId}")
    public ExemplaireLivre getExemplaireWithLivreIdAndBibliothequeId(@PathVariable("livreId") long livreId, @PathVariable("bibliothequeId") long bibliothequeId) {

        ExemplaireLivre exemplaireLivre = exemplaireLivreService.findExemplaireLivreByLivreIdAndBibliothequeId(livreId, bibliothequeId);

        return exemplaireLivre;
    }

    /**
     * Enregistre un exemplaire.
     */
    @PostMapping("/ajoutExemplaire")
    public ExemplaireLivre addExemplaire(@RequestBody ExemplaireLivre exemplaireLivre) {
        return exemplaireLivreService.save(exemplaireLivre);
    }

    /**
     * Permet de modifier un exemplaire.
     */
    @PutMapping(value = "/modifExemplaire")
    public ExemplaireLivre updateExemplaire(@RequestBody ExemplaireLivre exemplaireLivre) {
        return exemplaireLivreService.save(exemplaireLivre);
    }

}
