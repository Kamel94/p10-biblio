package fr.biblio.controller;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.configuration.Constantes;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.proxies.PretProxy;
import fr.biblio.service.contract.PretService;
import fr.biblio.service.contract.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class ReservationController {

    @Autowired
    private PretProxy pretProxy;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PretService pretService;

    /**
     * Affiche la liste des réservations.
     */
    @GetMapping(value = "/reservations")
    public List<Reservation> getReservationList() {
        return reservationService.findAll();
    }

    /**
     * Affiche la liste des réservations de par l'ID de l'utilisateur.
     */
    @GetMapping(value = "/reservationsByUtilisateurId/{utilisateurId}")
    public List<Reservation> getReservationListByUtilisateurId(@PathVariable("utilisateurId") long utilisateurId) {
        return reservationService.findAllByUtilisateurId(utilisateurId);
    }

    /**
     * Affiche la liste des réservations de par l'ID de l'exemplaire.
     */
    @GetMapping(value = "/reservationsByExemplaireId/{exemplaireId}")
    public List<Reservation> getReservationListByExemplaireId(@PathVariable("exemplaireId") long exemplaireId) {
        return reservationService.findAllByExemplaireId(exemplaireId);
    }

    /**
     * Affiche la liste des réservations de par le statut.
     */
    @GetMapping(value = "/reservationsByStatut/{statut}")
    public List<Reservation> getReservationListByStatut(@PathVariable("statut") String statut) {
        return reservationService.findAllByStatut(statut);
    }

    /**
     * Affiche la liste des réservations de par le statut et la notification.
     */
    @GetMapping(value = "/reservationsByStatutAndNotification/{statut}/{notification}")
    public List<Reservation> getReservationListByStatutAndNotificationDate(@PathVariable("statut") String statut,
                                                                           @PathVariable("notification") Date notification) {
        return reservationService.findAllByStatutAndNotificationDate(statut, notification);
    }

    /**
     * Affiche la liste des réservations de par le statut et l'ID de l'exemplaire.
     */
    @GetMapping(value = "/reservationsByStatutNotLikeAndExemplaireId/{statut}/{exemplaireId}")
    public List<Reservation> getReservationListByStatutNotLikeAndExemplaireId(@PathVariable("statut") String statut,
                                                                              @PathVariable("exemplaireId") long exemplaireId) {
        return reservationService.findAllByStatutNotLikeAndExemplaireId(statut, exemplaireId);
    }

    /**
     * Affiche une réservation de par l'ID de l'utilisateur et l'ID de l'exemplaire.
     */
    @GetMapping(value = "/reservationByUtilisateurIdAndExemplaireId/{utilisateurId}/{exemplaireId}")
    public Reservation getReservationByUtilisateurIdAndExemplaireId(@PathVariable("utilisateurId") long utilisateurId,
                                                                    @PathVariable("exemplaireId") long exemplaireId) {
        return reservationService.findByUtilisateurIdAndExemplaireId(utilisateurId, exemplaireId);
    }

    /**
     * Affiche une réservation de par son ID.
     */
    @GetMapping(value = "/reservations/{id}")
    public Reservation getReservation(@PathVariable("id") long id) {
        return reservationService.findById(id);
    }

    /**
     * Permet d'enregistrer une réservation.
     */
    @PostMapping(value = "/ajoutReservation/{utilisateurId}/{exemplaireId}")
    public Reservation addBooking(@PathVariable("utilisateurId") long utilisateurId,
                                  @PathVariable("exemplaireId") long exemplaireId) {

        Reservation reservation = new Reservation();
        List<Pret> pretsByExemplaireId = pretService.findByStatutAndExemplaireId(Constantes.PRET, exemplaireId);
        List<Reservation> reservationList = reservationService.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, exemplaireId);
        Pret pretWithStatutPret = pretService.findByUtilisateurIdAndExemplaireIdAndStatut(utilisateurId, exemplaireId, Constantes.PRET);
        Reservation reservationByUtilisateur = reservationService.findByUtilisateurIdAndExemplaireId(utilisateurId, exemplaireId);
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(exemplaireId);

        reservationService.addBookingService(reservation, exemplaireLivre, reservationList, pretsByExemplaireId, reservationByUtilisateur, pretWithStatutPret, utilisateurId);

        return reservationService.save(reservation);
    }

    @PutMapping(value = "/updateReservation/{id}")
    public Reservation updateReservation(@PathVariable("id") long id) {
        Reservation reservation = reservationService.findById(id);
        reservation.setNotificationDate(new Date());
        return reservationService.save(reservation);
    }

    @PutMapping(value = "/cancelReservation/{id}")
    public Reservation cancelReservation(@PathVariable("id") long id) {
        Reservation reservation = reservationService.findById(id);
        reservation.setStatut(Constantes.ANNULEE);
        reservationService.save(reservation);

        List<Reservation> reservationList = reservationService.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, reservation.getExemplaireId());
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(reservation.getExemplaireId());

        reservationService.updateStatutOrNombreExemplaire(reservationList, exemplaireLivre);

        if (!reservationList.isEmpty()) {
            reservationService.save(reservationList.get(0));
        }

        pretProxy.updateExemplaire(exemplaireLivre);

        return reservation;
    }
}
