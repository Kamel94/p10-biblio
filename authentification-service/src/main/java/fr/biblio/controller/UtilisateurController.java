package fr.biblio.controller;

import fr.biblio.entities.Utilisateur;
import fr.biblio.service.contract.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Affiche la liste des utilisateurs.
     */
    @GetMapping(value = "/listeUtilisateurs")
    public List<Utilisateur> listeUtilisateurs() {
        return utilisateurService.findAll();
    }

    /**
     * Cherche un utilisateur avec son ID.
     */
    @GetMapping(value = "/utilisateur/{id}")
    public Utilisateur getUtilisateur(@PathVariable("id") long id) {
        return utilisateurService.findById(id);
    }

    /**
     * Cherche un utilisateur avec son email.
     */
    @GetMapping(value = "/utilisateurByEmail/{email}")
    public Utilisateur getUtilisateurWithEmail(@PathVariable("email") String email) {
        return utilisateurService.findByEmail(email);
    }

    /**
     * Cherche un utilisateur avec son pseudo.
     */
    @GetMapping(value = "/utilisateurByPseudo/{pseudo}")
    public Utilisateur getUtilisateurWithPseudo(@PathVariable("pseudo") String pseudo) {
        return utilisateurService.findByPseudo(pseudo);
    }

    /**
     * Enregistre un utilisateur et encode son mot de passe.
     */
    @PostMapping(value = "/ajoutUtilisateur")
    public Utilisateur ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {
        return utilisateurService.save(utilisateur);
    }

}
