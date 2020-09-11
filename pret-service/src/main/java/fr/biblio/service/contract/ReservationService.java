package fr.biblio.service.contract;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;

import java.util.Date;
import java.util.List;

public interface ReservationService {

    void addBookingService(Reservation reservation, ExemplaireLivre exemplaireLivre, List<Reservation> reservationList, List<Pret> pretsByExemplaireId,
                           Reservation reservationByUtilisateur, Pret pretWithStatutPret, long utilisateurId);
    void updateStatutOrNombreExemplaire(List<Reservation> reservationList, ExemplaireLivre exemplaireLivre);

    List<Reservation> findAll();
    List<Reservation> findAllByStatutAndExemplaireId(String statut, long exemplaireId);
    List<Reservation> findAllByStatutNotLikeAndExemplaireId(String statut, long exemplaireId);
    List<Reservation> findAllByStatutAndNotificationDate(String statut, Date notification);
    List<Reservation> findAllByUtilisateurId(long utilisateurId);
    List<Reservation> findAllByExemplaireId(long exemplaireId);
    List<Reservation> findAllByStatut(String statut);
    Reservation findByUtilisateurIdAndExemplaireId(long utilisateurId, long exemplaireId);
    Reservation save(Reservation reservation);
    Reservation findById(long id);

}
