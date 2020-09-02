package fr.biblio.service;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.configuration.Constantes;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.exception.FunctionalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ServiceReservation {

    Logger log = LoggerFactory.getLogger(ServiceReservation.class);

    public void addBookingService(Reservation reservation, ExemplaireLivre exemplaireLivre, List<Reservation> reservationList, List<Pret> pretsByExemplaireId,
                                  Reservation reservationByUtilisateur, Pret pretWithStatutPret, long utilisateurId) {


        int nombreExemplaire = exemplaireLivre.getNombreExemplaire() + pretsByExemplaireId.size();

        if (nombreExemplaire * 2 > reservationList.size() && pretWithStatutPret == null &&
                reservationByUtilisateur == null) {
            log.info("Vous êtes sur la liste d'attente pour le livre '" + exemplaireLivre.getLivre().getTitre() + "'." +
                    "\nOn vous préviendra une fois que vous pourrez venir le chercher.");
            reservation.setBooking(new Date());
            reservation.setUtilisateurId(utilisateurId);
            reservation.setExemplaireId(exemplaireLivre.getId());
            reservation.setStatut(Constantes.EN_ATTENTE);

        } else if (nombreExemplaire * 2 <= reservationList.size() && pretWithStatutPret == null &&
                reservationByUtilisateur == null) {
            log.info("Le livre '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible...");
            throw new FunctionalException("Le livre '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible...");

        } else if (pretWithStatutPret != null) {
            log.info("Vous avez déjà un emprunt en cours sur ce livre.");
            throw new FunctionalException("Vous avez déjà un emprunt en cours sur ce livre.");

        } else if (reservationByUtilisateur != null) {
            log.info("Vous avez déjà une réservation en cours sur ce livre.");
            throw new FunctionalException("Vous avez déjà une réservation en cours sur ce livre.");
        }
    }

    public void updateStatutOrNombreExemplaire(List<Reservation> reservationList, ExemplaireLivre exemplaireLivre) {

        if (!reservationList.isEmpty()) {
            try {
                reservationList.get(0).setStatut(Constantes.MIS_A_DISPO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (reservationList.isEmpty()) {
            exemplaireLivre.setNombreExemplaire(exemplaireLivre.getNombreExemplaire() + 1);
        }

        if (exemplaireLivre.getNombreExemplaire() > 0) {
            exemplaireLivre.setDisponibilite(true);
        }
    }
}
