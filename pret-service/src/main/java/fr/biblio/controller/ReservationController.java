package fr.biblio.controller;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.configuration.Constantes;
import fr.biblio.dao.PretRepository;
import fr.biblio.dao.ReservationRepository;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.proxies.PretProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class ReservationController {

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PretProxy pretProxy;

    Logger log = LoggerFactory.getLogger(PretController.class);

    /**
     * Affiche la liste des réservations.
     */
    @GetMapping(value = "/reservations")
    public List<Reservation> getReservationList() {
        return reservationRepository.findAll();
    }

    /**
     * Affiche la liste des réservations de par l'ID de l'utilisateur.
     */
    @GetMapping(value = "/reservationsByUtilisateurId/{utilisateurId}")
    public List<Reservation> getReservationListByUtilisateurId(@PathVariable("utilisateurId") long utilisateurId) {
        return reservationRepository.findAllByUtilisateurId(utilisateurId);
    }

    /**
     * Affiche la liste des réservations de par l'ID de l'exemplaire.
     */
    @GetMapping(value = "/reservationsByExemplaireId/{exemplaireId}")
    public List<Reservation> getReservationListByExemplaireId(@PathVariable("exemplaireId") long exemplaireId) {
        return reservationRepository.findAllByExemplaireId(exemplaireId);
    }

    /**
     * Affiche la liste des réservations de par le statut.
     */
    @GetMapping(value = "/reservationsByStatut/{statut}")
    public List<Reservation> getReservationListByStatut(@PathVariable("statut") String statut) {
        return reservationRepository.findAllByStatut(statut);
    }

    /**
     * Affiche la liste des réservations de par le statut et la notification.
     */
    @GetMapping(value = "/reservationsByStatutAndNotification/{statut}/{notification}")
    public List<Reservation> getReservationListByStatutAndNotification(@PathVariable("statut") String statut,
                                                                       @PathVariable("notification") boolean notification) {
        return reservationRepository.findAllByStatutAndNotification(statut, notification);
    }

    /**
     * Affiche la liste des réservations de par le statut et l'ID de l'exemplaire.
     */
    @GetMapping(value = "/reservationsByStatutNotLikeAndExemplaireId/{statut}/{exemplaireId}")
    public List<Reservation> getReservationListByStatutNotLikeAndExemplaireId(@PathVariable("statut") String statut,
                                                                       @PathVariable("exemplaireId") long exemplaireId) {
        return reservationRepository.findAllByStatutNotLikeAndExemplaireId(statut, exemplaireId);
    }

    /**
     * Affiche une réservation de par l'ID de l'utilisateur et l'ID de l'exemplaire.
     */
    @GetMapping(value = "/reservationByUtilisateurIdAndExemplaireId/{utilisateurId}/{exemplaireId}")
    public Reservation getReservationByUtilisateurIdAndExemplaireId(@PathVariable("utilisateurId") long utilisateurId,
                                                                    @PathVariable("exemplaireId") long exemplaireId) {
        return reservationRepository.findByUtilisateurIdAndExemplaireId(utilisateurId, exemplaireId);
    }

    /**
     * Affiche une réservation de par son ID.
     */
    @GetMapping(value = "/reservations/{id}")
    public Reservation getReservation(@PathVariable("id") long id) {
        return reservationRepository.getOne(id);
    }

    /**
     * Permet d'enregistrer une réservation.
     */
    @PostMapping(value = "/ajoutReservation/{utilisateurId}/{exemplaireId}")
    public Reservation addBooking(@PathVariable("utilisateurId") long utilisateurId,
                        @PathVariable("exemplaireId") long exemplaireId) {

        Reservation reservation = new Reservation();
        Reservation reservationByUtilisateur = reservationRepository.findByUtilisateurIdAndExemplaireId(utilisateurId, exemplaireId);
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(exemplaireId);
        List<Pret> pretsByExemplaireId = pretRepository.findByStatutAndExemplaireId(Constantes.PRET, exemplaireId);
        List<Reservation> reservationList = reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, exemplaireId);
        Pret pretWithStatutPret = pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(utilisateurId, exemplaireId, Constantes.PRET);

        int nombreExemplaire = exemplaireLivre.getNombreExemplaire() + pretsByExemplaireId.size();
        System.out.println(nombreExemplaire);
        System.out.println(reservationList.size());

        if(nombreExemplaire * 2 > reservationList.size() && pretWithStatutPret == null &&
                reservationByUtilisateur == null) {
            log.info("Vous êtes sur la liste d'attente pour le livre '" + exemplaireLivre.getLivre().getTitre() + "'." +
                    "\nOn vous préviendra une fois que vous pourrez venir le chercher.");
            reservation.setBooking(new Date());
            reservation.setUtilisateurId(utilisateurId);
            reservation.setNotification(false);
            reservation.setExemplaireId(exemplaireId);
            reservation.setStatut(Constantes.EN_ATTENTE);

        } else if (nombreExemplaire * 2 <= reservationList.size() && pretWithStatutPret == null &&
                reservationByUtilisateur == null) {
            log.info("Le livre '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible...");
            System.out.println(nombreExemplaire);
            return null;

        } else if (pretWithStatutPret != null) {
            log.info("Vous avez déjà un emprunt en cours sur ce livre.");
            return null;
        } else if (reservationByUtilisateur != null) {
            log.info("Vous avez déjà une réservation en cours sur ce livre.");
            return null;
        }
        return reservationRepository.save(reservation);
    }

    @PutMapping(value = "/updateReservation/{id}")
    public Reservation updateReservation(@PathVariable("id") long id) {
        Reservation reservation = reservationRepository.getOne(id);
        reservation.setNotification(true);
        reservation.setNotificationDate(new Date());
        return reservationRepository.save(reservation);
    }

    @PutMapping(value = "/cancelReservation/{id}")
    public Reservation cancelReservation(@PathVariable("id") long id) {
        Reservation reservation = reservationRepository.getOne(id);
        List<Reservation> reservationList = reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, reservation.getExemplaireId());
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(reservation.getExemplaireId());
        reservation.setStatut(Constantes.ANNULEE);

        if (!reservationList.isEmpty()) {
            try {
                reservationList.get(0).setStatut(Constantes.MIS_A_DISPO);
            } catch (Exception e) {
                e.printStackTrace();
            }
            reservationRepository.save(reservationList.get(0));
        } else if (reservationList.isEmpty()) {
            exemplaireLivre.setNombreExemplaire(exemplaireLivre.getNombreExemplaire() + 1);
        }

        if (exemplaireLivre.getNombreExemplaire() > 0) {
            exemplaireLivre.setDisponibilite(true);
        }
        pretProxy.updateExemplaire(exemplaireLivre);

        return reservationRepository.save(reservation);
    }
}