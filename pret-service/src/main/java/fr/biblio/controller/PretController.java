package fr.biblio.controller;

import fr.biblio.beans.Bibliotheque;
import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.beans.LivreBean;
import fr.biblio.configuration.Constantes;
import fr.biblio.entities.Reservation;
import fr.biblio.proxies.PretProxy;
import fr.biblio.entities.Pret;
import fr.biblio.service.contract.PretService;
import fr.biblio.service.contract.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class PretController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PretService pretService;

    @Autowired
    private PretProxy pretProxy;

    /**
     * Affiche la liste des prêts.
     */
    @GetMapping(value = "/prets")
    public List<Pret> listeDesPrets() {
        return pretService.findAll();
    }

    /**
     * Affiche un prêt de par son ID.
     */
    @GetMapping(value = "/prets/{id}")
    public Pret getPret(@PathVariable("id") long id) {
        return pretService.findById(id);
    }

    /**
     * Affiche un prêt de par son l'ID de l'utilisateur, l'ID de l'exemplaire et le statut.
     */
    @GetMapping(value = "/prets/{utilisateurId}/{exemplaireId}/{statut}")
    public Pret getPretWithUtilisateurIdAndExemplaireIdAndStatut(@PathVariable("utilisateurId") long utilisateurId,
                                                                 @PathVariable("exemplaireId") long exemplaireId,
                                                                 @PathVariable("statut") String statut) {
        return pretService.findByUtilisateurIdAndExemplaireIdAndStatut(utilisateurId, exemplaireId, statut);
    }

    /**
     * Affiche un prêt de par son l'ID de l'utilisateur et l'ID de l'exemplaire.
     */
    @GetMapping(value = "/prets/{utilisateurId}/{exemplaireId}/notLike/{statut}")
    public Pret getPretWithUtilisateurIdAndExemplaireId(@PathVariable("utilisateurId") long utilisateurId,
                                                        @PathVariable("exemplaireId") long exemplaireId,
                                                        @PathVariable("statut") String statut) {
        return pretService.findByUtilisateurIdAndExemplaireIdAndStatutNotLike(utilisateurId, exemplaireId, statut);
    }

    /**
     * Affiche la liste des prêts d'un utilisateur avec le statut "PRET".
     */
    @GetMapping(value = "/pretUtilisateur/{utilisateurId}")
    public List<Pret> getPretsWithUtilisateurId(@PathVariable("utilisateurId") long utilisateurId) {

        List<Pret> prets = pretService.findByUtilisateurIdAndStatut(utilisateurId, Constantes.PRET);
        for (Pret pret : prets) {
            ExemplaireLivre exemplaire = pretProxy.getExemplaire(pret.getExemplaireId());
            LivreBean livre = pretProxy.getLivre(exemplaire.getLivreId());
            Bibliotheque bibliotheque = pretProxy.getBibliotheque(exemplaire.getBibliothequeId());

            pret.setTitreLivre(livre.getTitre());
            pret.setNumeroSerieExemplaire(exemplaire.getNumeroSerie());
            pret.setNomBiblio(bibliotheque.getNom());
        }
        return prets;
    }

    /**
     * Affiche la liste des prêts d'un utilisateur.
     */
    @GetMapping(value = "/pretUtilisateur/{utilisateurId}/{statut}")
    public List<Pret> getPretsWithUtilisateurIdAndStatut(@PathVariable("utilisateurId") long utilisateurId, @PathVariable("statut") String statut) {

        List<Pret> prets = pretService.findByUtilisateurIdAndStatut(utilisateurId, statut);
        return prets;
    }

    /**
     * Affiche la liste des prêts avec le statut "PRET" et l'id de l'exemplaire.
     */
    @GetMapping(value = "/pretsWithStatutPretAndExemplaireId/{exemplaireId}")
    public List<Pret> getPretsWithStatutPretAndExemplaireId(@PathVariable("exemplaireId") long exemplaireId) {

        List<Pret> prets = pretService.findByStatutAndExemplaireId(Constantes.PRET, exemplaireId);
        for (Pret pret : prets) {
            ExemplaireLivre exemplaire = pretProxy.getExemplaire(pret.getExemplaireId());
            LivreBean livre = pretProxy.getLivre(exemplaire.getLivreId());
            Bibliotheque bibliotheque = pretProxy.getBibliotheque(exemplaire.getBibliothequeId());

            pret.setTitreLivre(livre.getTitre());
            pret.setNumeroSerieExemplaire(exemplaire.getNumeroSerie());
            pret.setNomBiblio(bibliotheque.getNom());
            pret.setNombreExemplaire(exemplaire.getNombreExemplaire());
        }
        return prets;
    }

    /**
     * Affiche la liste des prêts avec le statut "PRET" et l'id de l'exemplaire.
     */
    @GetMapping(value = "/pretsOrderByDateRetourAsc/{exemplaireId}")
    public List<Pret> getPretsOrderByDateRetourAsc(@PathVariable("exemplaireId") long exemplaireId) {

        List<Pret> prets = pretService.findPretByStatutAndExemplaireIdOrderByDateRetourAsc(Constantes.PRET, exemplaireId);
        return prets;
    }

    /**
     * Affiche la liste des prêts avec le statut et l'id de l'exemplaire.
     */
    @GetMapping(value = "/pretsWithStatutAndExemplaireId/{statut}/{exemplaireId}")
    public List<Pret> getPretsWithStatutAndExemplaireId(@PathVariable("statut") String statut, @PathVariable("exemplaireId") long exemplaireId) {

        List<Pret> prets = pretService.findByStatutAndExemplaireId(statut, exemplaireId);
        return prets;
    }

    /**
     * Affiche la liste des prêts par le statut.
     */
    @GetMapping(value = "/pretsByStatut/{statut}")
    public List<Pret> getPretsByStatut(@PathVariable("statut") String statut) {

        List<Pret> prets = pretService.findPretByStatut(statut);
        return prets;
    }

    /**
     * Affiche la liste des prêts dont la date du prêt est passé.
     */
    @GetMapping(value = "/dateRetourPassee")
    public List<Pret> getPretsFinished() {
        Date date = new Date();
        List<Pret> prets = pretService.findPretByStatutAndDateRetourBefore(Constantes.PRET, date);
        return prets;
    }

    /**
     * Permet de modifier le statut et la date retour une fois le livre rendu.
     */
    @PostMapping(value = "/livreRendu/{pretId}")
    public Pret rendreLivre(@PathVariable("pretId") long pretId) {

        Pret pret = pretService.findById(pretId);
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(pret.getExemplaireId());
        List<Reservation> reservationList = reservationService.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, exemplaireLivre.getId());

        System.out.println("Controller = " + pret.getStatut());

        pretService.returnBook(pret, reservationList, exemplaireLivre);
        if (!reservationList.isEmpty()) {
            reservationService.save(reservationList.get(0));
        }
        pretProxy.updateExemplaire(exemplaireLivre);

        return pretService.save(pret);
    }

    /**
     * Permet de prolonger un prêt.
     */
    @PostMapping(value = "/prolongation/{pretId}")
    public Pret prolongerPret(@PathVariable("pretId") long pretId) {

        Pret pret = pretService.findById(pretId);
        pretService.extendLoan(pret);
        return pretService.save(pret);
    }

    /**
     * Permet d'enregistrer un prêt.
     */
    @PostMapping(value = "/ajoutPret/{utilisateurId}/{exemplaireId}")
    public Pret addPret(@PathVariable("utilisateurId") long utilisateurId,
                        @PathVariable("exemplaireId") long exemplaireId) {

        Pret pretWithStatutPret = pretService.findByUtilisateurIdAndExemplaireIdAndStatut(utilisateurId, exemplaireId, Constantes.PRET);
        Reservation reservationByUtilisateur = reservationService.findByUtilisateurIdAndExemplaireId(utilisateurId, exemplaireId);
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(exemplaireId);
        Pret pret = new Pret();

        if (pretService.checkLoan(exemplaireLivre, pretWithStatutPret, reservationByUtilisateur).equals(Constantes.NOUVEAU_PRET)) {
            pretProxy.updateExemplaire(exemplaireLivre);
            if (reservationByUtilisateur != null) {
                reservationService.save(reservationByUtilisateur);
            }
            pret = pretService.addNewPret(utilisateurId, exemplaireId);
        }

        return pretService.save(pret);
    }

    /**
     * Supprime un prêt.
     */
    @PostMapping(value = "/delete/{id}")
    public Pret delete(@PathVariable("id") long id) {
        return pretService.deleteById(id);
    }
}