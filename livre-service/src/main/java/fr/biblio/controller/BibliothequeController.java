package fr.biblio.controller;

import fr.biblio.entities.Bibliotheque;
import fr.biblio.service.contract.BibliothequeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BibliothequeController {

    @Autowired
    private BibliothequeService bibliothequeService;

    /**
     * Affiche la liste des bibliothèques.
     */
    @GetMapping(value = "/listeBibliotheques")
    public List<Bibliotheque> listeDesBibliotheques() {
        List<Bibliotheque> bibliotheques = bibliothequeService.findAll();
        return bibliotheques;
    }

    /**
     * Affiche une bibliothèque de par son ID.
     */
    @GetMapping(value = "/listeBibliotheques/{id}")
    public Bibliotheque getBibliotheque(@PathVariable("id") long id) {
        return bibliothequeService.findById(id);
    }

    /**
     * Permet d'enregistrer une bibliothèque.
     */
    @PostMapping(value = "/ajoutBibliotheque")
    public Bibliotheque ajouterUneBibliotheque(@RequestBody Bibliotheque bibliotheque) {
        return bibliothequeService.save(bibliotheque);
    }

}
