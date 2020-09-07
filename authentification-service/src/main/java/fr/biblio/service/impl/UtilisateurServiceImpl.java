package fr.biblio.service.impl;

import fr.biblio.dao.UtilisateurRepository;
import fr.biblio.entities.Utilisateur;
import fr.biblio.service.contract.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    @Autowired
    private UtilisateurRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Pour encoder le mot de passe de l'utilisateur.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public List<Utilisateur> findAll() {
        return repository.findAll();
    }

    @Override
    public Utilisateur findById(long id) {
        return repository.findById(id).get();
    }

    @Override
    public Utilisateur findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Utilisateur findByPseudo(String pseudo) {
        return repository.findByPseudo(pseudo);
    }

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        String passEncoder = passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(passEncoder);
        return repository.save(utilisateur);
    }
}
