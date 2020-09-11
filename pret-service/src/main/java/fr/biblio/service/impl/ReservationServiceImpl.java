package fr.biblio.service.impl;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.configuration.Constantes;
import fr.biblio.dao.ReservationRepository;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.exception.FunctionalException;
import fr.biblio.service.contract.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    Logger log = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Autowired
    private ReservationRepository repository;

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

    @Override
    public List<Reservation> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Reservation> findAllByStatutAndExemplaireId(String statut, long exemplaireId) {
        return repository.findAllByStatutAndExemplaireId(statut, exemplaireId);
    }

    @Override
    public List<Reservation> findAllByStatutNotLikeAndExemplaireId(String statut, long exemplaireId) {
        return repository.findAllByStatutNotLikeAndExemplaireId(statut, exemplaireId);
    }

    @Override
    public List<Reservation> findAllByStatutAndNotificationDate(String statut, Date notification) {
        return repository.findAllByStatutAndNotificationDate(statut, notification);
    }

    @Override
    public List<Reservation> findAllByUtilisateurId(long utilisateurId) {
        return repository.findAllByUtilisateurId(utilisateurId);
    }

    @Override
    public List<Reservation> findAllByExemplaireId(long exemplaireId) {
        return repository.findAllByExemplaireId(exemplaireId);
    }

    @Override
    public List<Reservation> findAllByStatut(String statut) {
        return repository.findAllByStatut(statut);
    }

    @Override
    public Reservation findByUtilisateurIdAndExemplaireId(long utilisateurId, long exemplaireId) {
        return repository.findByUtilisateurIdAndExemplaireId(utilisateurId, exemplaireId);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return repository.save(reservation);
    }

    @Override
    public Reservation findById(long id) {
        return repository.findById(id).get();
    }
}
