package fr.biblio.proxy;

import fr.biblio.beans.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Date;
import java.util.List;

@FeignClient(name = "zuul-server", url = "localhost:8888")
public interface BatchProxy {

    @GetMapping(value = "/authentification-service/utilisateur/{id}")
    Utilisateur getUtilisateur(@PathVariable("id") long id);

    @GetMapping(value = "/livre-service/livres/{id}")
    Livre getLivre(@PathVariable("id") long id);

    @GetMapping("/livre-service/exemplaireLivre/{id}")
    ExemplaireLivre getExemplaire(@PathVariable("id") long id);

    @GetMapping(value = "/livre-service/listeBibliotheques/{id}")
    Bibliotheque getBibliotheque(@PathVariable("id") long id);

    @GetMapping(value = "/pret-service/dateRetourPassee")
    List<Pret> getPretsFinished();

    @GetMapping(value = "/pret-service/reservations")
    List<Reservation> getReservationList();

    @GetMapping(value = "/pret-service/reservationsByExemplaireId/{exemplaireId}")
    List<Reservation> getReservationListByExemplaireId(@PathVariable("exemplaireId") long exemplaireId);

    @GetMapping(value = "/pret-service/reservationsByStatut/{statut}")
    List<Reservation> getReservationListByStatut(@PathVariable("statut") String statut);

    @GetMapping(value = "/pret-service/reservationsByStatutAndNotification/{statut}/{notification}")
    List<Reservation> getReservationListByStatutAndNotificationDate(@PathVariable("statut") String statut,
                                                                @PathVariable("notification") Date notification);

    @PutMapping(value = "/pret-service/updateReservation/{id}")
    Reservation updateReservation(@PathVariable("id") long id);

    @PutMapping(value = "/pret-service/cancelReservation/{id}")
    Reservation cancelReservation(@PathVariable("id") long id);

}
