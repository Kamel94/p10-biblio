package fr.biblio.controller;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.configuration.Constantes;
import fr.biblio.dao.PretRepository;
import fr.biblio.dao.ReservationRepository;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.proxies.PretProxy;
import fr.biblio.service.ServiceReservation;
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

    @Autowired
    private ServiceReservation serviceReservation;

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
    public List<Reservation> getReservationListByStatutAndNotificationDate(@PathVariable("statut") String statut,
                                                                           @PathVariable("notification") Date notification) {
        return reservationRepository.findAllByStatutAndNotificationDate(statut, notification);
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
        return reservationRepository.findById(id).get();
    }

    /**
     * Permet d'enregistrer une réservation.
     */
    @PostMapping(value = "/ajoutReservation/{utilisateurId}/{exemplaireId}")
    public Reservation addBooking(@PathVariable("utilisateurId") long utilisateurId,
                                  @PathVariable("exemplaireId") long exemplaireId) {

        Reservation reservation = new Reservation();
        List<Pret> pretsByExemplaireId = pretRepository.findByStatutAndExemplaireId(Constantes.PRET, exemplaireId);
        List<Reservation> reservationList = reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, exemplaireId);
        Pret pretWithStatutPret = pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(utilisateurId, exemplaireId, Constantes.PRET);
        Reservation reservationByUtilisateur = reservationRepository.findByUtilisateurIdAndExemplaireId(utilisateurId, exemplaireId);
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(exemplaireId);

        serviceReservation.addBookingService(reservation, exemplaireLivre, reservationList, pretsByExemplaireId, reservationByUtilisateur, pretWithStatutPret, utilisateurId);

        return reservationRepository.save(reservation);
    }

    @PutMapping(value = "/updateReservation/{id}")
    public Reservation updateReservation(@PathVariable("id") long id) {
        Reservation reservation = reservationRepository.findById(id).get();
        reservation.setNotificationDate(new Date());
        return reservationRepository.save(reservation);
    }

    @PutMapping(value = "/cancelReservation/{id}")
    public Reservation cancelReservation(@PathVariable("id") long id) {
        Reservation reservation = reservationRepository.findById(id).get();
        reservation.setStatut(Constantes.ANNULEE);
        reservationRepository.save(reservation);

        List<Reservation> reservationList = reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, reservation.getExemplaireId());
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(reservation.getExemplaireId());

        serviceReservation.updateStatutOrNombreExemplaire(reservationList, exemplaireLivre);

        if (!reservationList.isEmpty()) {
            reservationRepository.save(reservationList.get(0));
        }

        pretProxy.updateExemplaire(exemplaireLivre);

        return reservation;
    }
}
