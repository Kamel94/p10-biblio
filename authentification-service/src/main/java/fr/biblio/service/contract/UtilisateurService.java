package fr.biblio.service.contract;

import fr.biblio.entities.Utilisateur;

import java.util.List;

public interface UtilisateurService {

    List<Utilisateur> findAll();
    Utilisateur findById(long id);
    Utilisateur findByEmail(String email);
    Utilisateur findByPseudo(String pseudo);
    Utilisateur save(Utilisateur utilisateur);
}
