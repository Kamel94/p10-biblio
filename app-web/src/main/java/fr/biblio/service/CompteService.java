package fr.biblio.service;

import fr.biblio.beans.Utilisateur;

public interface CompteService {

    Utilisateur saveUser(Utilisateur utilisateur);
    Utilisateur findByPseudo(String pseudo);

}
