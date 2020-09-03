package fr.biblio.service;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.configuration.Constantes;
import fr.biblio.controller.PretController;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.exception.FunctionalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class ServicePret {

    Logger log = LoggerFactory.getLogger(ServicePret.class);

    public Pret addNewPret(long utilisateurId, long exemplaireId) {

        Pret pret = new Pret();

        try {
            GregorianCalendar date = new GregorianCalendar();

            pret.setDatePret(new Date());
            date.setTime(pret.getDatePret());
            date.add(GregorianCalendar.DAY_OF_YEAR, +28);
            pret.setUtilisateurId(utilisateurId);
            pret.setDateRetour(date.getTime());
            pret.setProlongation(0);
            pret.setExemplaireId(exemplaireId);
            pret.setStatut(Constantes.PRET);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pret;
    }

    public String checkLoan(ExemplaireLivre exemplaireLivre, Pret pretWithStatutPret, Reservation reservationByUtilisateur) {

        if (!exemplaireLivre.isDisponibilite() && pretWithStatutPret == null &&
                reservationByUtilisateur == null) {
            log.info("L'exemplaire '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible..." +
                    "\nMerci de faire une réservation.");
            throw new FunctionalException("Exemplaire non disponible.");

        } else if (exemplaireLivre.isDisponibilite() && pretWithStatutPret == null &&
                reservationByUtilisateur == null) {
            exemplaireLivre.setNombreExemplaire(exemplaireLivre.getNombreExemplaire() - 1);

            if (exemplaireLivre.getNombreExemplaire() == 0) {
                exemplaireLivre.setDisponibilite(false);
            }
            log.info("Ajout d'un nouveau prêt.");

        } else if (reservationByUtilisateur != null && reservationByUtilisateur.getStatut().equals(Constantes.MIS_A_DISPO)) {
            log.info("Vous pouvez récupérer votre réservation.");
            reservationByUtilisateur.setStatut(Constantes.RECUPEREE);

        } else if (pretWithStatutPret != null) {
            log.info("Vous avez déjà un emprunt en cours sur ce livre.");
            throw new FunctionalException("Vous avez déjà un emprunt en cours sur ce livre.");

        } else if (reservationByUtilisateur != null) {
            log.info("Vous avez déjà une réservation en cours sur ce livre.");
            throw new FunctionalException("Vous avez déjà une réservation en cours sur ce livre.");
        }
        return Constantes.NOUVEAU_PRET;
    }

    public void extendLoan(Pret pret) {

        Date today = new Date();
        long todayLong = today.getTime();
        long dateRetourLong = pret.getDateRetour().getTime();

        if (pret.getProlongation() == 0 && todayLong < dateRetourLong) {
            try {
                GregorianCalendar date = new GregorianCalendar();

                date.setTime(pret.getDateRetour());
                date.add(GregorianCalendar.DAY_OF_YEAR, +28);

                pret.setDateRetour(date.getTime());
                pret.setProlongation(pret.getProlongation() + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("Le prêt a bien été prolongé.");

        } else if (pret.getProlongation() == 0 && todayLong >= dateRetourLong) {
            log.info("Vous ne pouvez plus prolonger ce prêt, car la date de retour du prêt est dépassée.");
            throw new FunctionalException("Vous ne pouvez plus prolonger ce prêt, car la date de retour du prêt est dépassée.");

        } else if (pret.getProlongation() >= 1) {
            log.info("Ce prêt a atteint le nombre maximum de prolongation...");
            throw new FunctionalException("Ce prêt a atteint le nombre maximum de prolongation...");
        }
    }

    public void returnBook(Pret pret, List<Reservation> reservationList, ExemplaireLivre exemplaireLivre) {

        System.out.println("Service = " + pret.getStatut());
        if (pret.getStatut().equals(Constantes.PRET)) {

            pret.setStatut(Constantes.RENDU);
            pret.setDateRetour(new Date());
            pret.setTitreLivre(exemplaireLivre.getLivre().getTitre());

            if (!reservationList.isEmpty()) {
                try {
                    reservationList.get(0).setStatut(Constantes.MIS_A_DISPO);
                    log.info("Mis à disposition du livre pour le prochain de la liste de réservation.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (reservationList.isEmpty()) {
                exemplaireLivre.setNombreExemplaire(exemplaireLivre.getNombreExemplaire() + 1);
                log.info("La liste de réservation est vide, donc il y a 1 exemplaire en plus qui est de nouveau disponible.");
            }

            if (exemplaireLivre.getNombreExemplaire() > 0) {
                exemplaireLivre.setDisponibilite(true);
            }
            log.info("Le livre '" + pret.getTitreLivre() + "' a été rendu.");

        } else {
            log.info("Ce prêt n'est pas en cours...");
            throw new FunctionalException("Ce prêt n'est pas en cours...");
        }
    }
}
