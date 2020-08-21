package fr.biblio.controller;

import fr.biblio.beans.Bibliotheque;
import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.beans.LivreBean;
import fr.biblio.configuration.Constantes;
import fr.biblio.dao.ReservationRepository;
import fr.biblio.entities.Reservation;
import fr.biblio.proxies.PretProxy;
import fr.biblio.dao.PretRepository;
import fr.biblio.entities.Pret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@RestController
public class PretController {

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationController reservationController;

    @Autowired
    private PretProxy pretProxy;

    Logger log = LoggerFactory.getLogger(PretController.class);

    /**
     * Affiche la liste des prêts.
     */
    @GetMapping(value = "/prets")
    public List<Pret> listeDesPrets() {
        return pretRepository.findAll();
    }

    /**
     * Affiche un prêt de par son ID.
     */
    @GetMapping(value = "/prets/{id}")
    public Pret getPret(@PathVariable("id") long id) {
        return pretRepository.findById(id).get();
    }

    /**
     * Affiche un prêt de par son l'ID de l'utilisateur, l'ID de l'exemplaire et le statut.
     */
    @GetMapping(value = "/prets/{utilisateurId}/{exemplaireId}/{statut}")
    public Pret getPretWithUtilisateurIdAndExemplaireIdAndStatut(@PathVariable("utilisateurId") long utilisateurId,
                                                                 @PathVariable("exemplaireId") long exemplaireId,
                                                                 @PathVariable("statut") String statut) {
        return pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(utilisateurId, exemplaireId, statut);
    }

    /**
     * Affiche un prêt de par son l'ID de l'utilisateur et l'ID de l'exemplaire.
     */
    @GetMapping(value = "/prets/{utilisateurId}/{exemplaireId}/notLike/{statut}")
    public Pret getPretWithUtilisateurIdAndExemplaireId(@PathVariable("utilisateurId") long utilisateurId,
                                                        @PathVariable("exemplaireId") long exemplaireId,
                                                        @PathVariable("statut") String statut) {
        return pretRepository.findByUtilisateurIdAndExemplaireIdAndStatutNotLike(utilisateurId, exemplaireId, statut);
    }

    /**
     * Affiche la liste des prêts d'un utilisateur avec le statut "PRET".
     */
    @GetMapping(value = "/pretUtilisateur/{utilisateurId}")
    public List<Pret> getPretsWithUtilisateurId(@PathVariable("utilisateurId") long utilisateurId) {

        List<Pret> prets = pretRepository.findByUtilisateurIdAndStatut(utilisateurId, Constantes.PRET);
        for (Pret pret : prets) {
            ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(pret.getExemplaireId());
            LivreBean livre = pretProxy.getLivre(exemplaireLivre.getLivreId());
            Bibliotheque bibliotheque = pretProxy.getBibliotheque(exemplaireLivre.getBibliothequeId());

            pret.setTitreLivre(livre.getTitre());
            pret.setNumeroSerieExemplaire(exemplaireLivre.getNumeroSerie());
            pret.setNomBiblio(bibliotheque.getNom());
        }
        return prets;
    }

    /**
     * Affiche la liste des prêts d'un utilisateur.
     */
    @GetMapping(value = "/pretUtilisateur/{utilisateurId}/{statut}")
    public List<Pret> getPretsWithUtilisateurIdAndStatut(@PathVariable("utilisateurId") long utilisateurId, @PathVariable("statut") String statut) {

        List<Pret> prets = pretRepository.findByUtilisateurIdAndStatut(utilisateurId, statut);
        return prets;
    }

    /**
     * Affiche la liste des prêts avec le statut "PRET" et l'id de l'exemplaire.
     */
    @GetMapping(value = "/pretsWithStatutPretAndExemplaireId/{exemplaireId}")
    public List<Pret> getPretsWithStatutPretAndExemplaireId(@PathVariable("exemplaireId") long exemplaireId) {

        List<Pret> prets = pretRepository.findByStatutAndExemplaireId(Constantes.PRET, exemplaireId);
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

        List<Pret> prets = pretRepository.findPretByStatutAndExemplaireIdOrderByDateRetourAsc(Constantes.PRET, exemplaireId);
        return prets;
    }

    /**
     * Affiche la liste des prêts avec le statut et l'id de l'exemplaire.
     */
    @GetMapping(value = "/pretsWithStatutAndExemplaireId/{statut}/{exemplaireId}")
    public List<Pret> getPretsWithStatutAndExemplaireId(@PathVariable("statut") String statut, @PathVariable("exemplaireId") long exemplaireId) {

        List<Pret> prets = pretRepository.findByStatutAndExemplaireId(statut, exemplaireId);
        return prets;
    }

    /**
     * Affiche la liste des prêts par le statut.
     */
    @GetMapping(value = "/pretsByStatut/{statut}")
    public List<Pret> getPretsByStatut(@PathVariable("statut") String statut) {

        List<Pret> prets = pretRepository.findPretByStatut(statut);
        return prets;
    }

    /**
     * Affiche la liste des prêts dont la date du prêt est passé.
     */
    @GetMapping(value = "/dateRetourPassee")
    public List<Pret> getPretsFinished() {
        Date date = new Date();
        List<Pret> prets = pretRepository.findPretByStatutAndDateRetourBefore(Constantes.PRET, date);
        return prets;
    }

    /**
     * Permet de modifier le statut et la date retour une fois le livre rendu.
     */
    @PostMapping(value = "/livreRendu/{pretId}")
    public Pret rendreLivre(@PathVariable("pretId") long pretId) {

        Pret pret = pretRepository.findById(pretId).get();
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(pret.getExemplaireId());
        List<Reservation> reservationList = reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, pret.getExemplaireId());
        int i = 0;

        if (pret.getStatut().equals(Constantes.PRET)) {

            if (reservationList.isEmpty()) {
                exemplaireLivre.setNombreExemplaire(exemplaireLivre.getNombreExemplaire() + 1);
            }

            if (exemplaireLivre.getNombreExemplaire() > 0) {
                exemplaireLivre.setDisponibilite(true);
            }

            pretProxy.updateExemplaire(exemplaireLivre);
            pret.setStatut(Constantes.RENDU);
            pret.setDateRetour(new Date());

                if (!reservationList.isEmpty() && pret.getExemplaireId() == reservationList.get(i).getExemplaireId()) {
                    try {
                        reservationList.get(i).setStatut(Constantes.MIS_A_DISPO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    reservationRepository.save(reservationList.get(i));
                }

        } else {
            log.info("Ce prêt n'est pas en cours...");
        }
        return pretRepository.save(pret);
    }

    /**
     * Permet de prolonger un prêt.
     */
    @PostMapping(value = "/prolongation/{pretId}")
    public Pret prolongerPret(@PathVariable("pretId") long pretId) {

        Pret pret = pretRepository.findById(pretId).get();

        if (pret.getProlongation() == 0) {
            try {
                GregorianCalendar date = new GregorianCalendar();

                date.setTime(pret.getDateRetour());
                date.add(GregorianCalendar.DAY_OF_YEAR, +28);

                pret.setDateRetour(date.getTime());
                pret.setProlongation(pret.getProlongation() + 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return pretRepository.save(pret);
        } else {
            log.info("Ce prêt a atteint le nombre maximum de prolongation...");
        }
        return null;
    }

    /**
     * Permet d'enregistrer un prêt.
     */
    @PostMapping(value = "/ajoutPret/{utilisateurId}/{exemplaireId}")
    public Pret addPret(@PathVariable("utilisateurId") long utilisateurId,
                          @PathVariable("exemplaireId") long exemplaireId) {

        Pret pret = new Pret();
        Pret pretWithStatutPret = pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(utilisateurId, exemplaireId, Constantes.PRET);
        Reservation reservationByUtilisateur = reservationRepository.findByUtilisateurIdAndExemplaireId(utilisateurId, exemplaireId);
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(exemplaireId);

        if(!exemplaireLivre.isDisponibilite() && pretWithStatutPret == null &&
                reservationByUtilisateur == null) {
            log.info("L'exemplaire '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible..." +
                    "\nMerci de faire une réservation.");

        } else if (exemplaireLivre.isDisponibilite() && pretWithStatutPret == null &&
                 reservationByUtilisateur == null) {
            exemplaireLivre.setNombreExemplaire(exemplaireLivre.getNombreExemplaire() - 1);

            if (exemplaireLivre.getNombreExemplaire() == 0) {
                exemplaireLivre.setDisponibilite(false);
            }
            pretProxy.updateExemplaire(exemplaireLivre);

            try {
                GregorianCalendar date = new GregorianCalendar();

                pret.setDatePret(new Date());
                date.setTime(pret.getDatePret());
                date.add(GregorianCalendar.DAY_OF_YEAR, + 28);
                pret.setUtilisateurId(utilisateurId);
                pret.setDateRetour(date.getTime());
                pret.setProlongation(0);
                pret.setExemplaireId(exemplaireLivre.getId());
                pret.setStatut(Constantes.PRET);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return pretRepository.save(pret);

        } else if (pretWithStatutPret == null && reservationByUtilisateur.getStatut().equals(Constantes.MIS_A_DISPO)) {
            log.info("Vous pouvez récupérer votre réservation.");
            reservationController.cancelReservation(reservationByUtilisateur.getId());
            try {
                GregorianCalendar date = new GregorianCalendar();

                pret.setDatePret(new Date());
                date.setTime(pret.getDatePret());
                date.add(GregorianCalendar.DAY_OF_YEAR, + 28);
                pret.setUtilisateurId(utilisateurId);
                pret.setDateRetour(date.getTime());
                pret.setProlongation(0);
                pret.setExemplaireId(exemplaireLivre.getId());
                pret.setStatut(Constantes.PRET);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return pretRepository.save(pret);

        } else if (pretWithStatutPret != null) {
            log.info("Vous avez déjà un emprunt en cours sur ce livre.");

        } else if (reservationByUtilisateur != null) {
            log.info("Vous avez déjà une réservation en cours sur ce livre.");
        }
        return null;
    }

    /**
     * Supprime un prêt.
     */
    @PostMapping(value = "/delete/{id}")
    public Pret delete(@PathVariable("id") long id) {
        return pretRepository.deleteById(id);
    }

}
