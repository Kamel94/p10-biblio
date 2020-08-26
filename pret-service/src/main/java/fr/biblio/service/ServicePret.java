package fr.biblio.service;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.configuration.Constantes;
import fr.biblio.controller.PretController;
import fr.biblio.dao.PretRepository;
import fr.biblio.dao.ReservationRepository;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.exception.FunctionalException;
import fr.biblio.proxies.PretProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.GregorianCalendar;

@Service
public class ServicePret {

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private PretProxy pretProxy;

    @Autowired
    private ReservationRepository reservationRepository;

    Logger log = LoggerFactory.getLogger(PretController.class);

    public Pret saveNewPret(long utilisateurId, long exemplaireId) {

        Pret pret = new Pret();

        try {
            GregorianCalendar date = new GregorianCalendar();

            pret.setDatePret(new Date());
            date.setTime(pret.getDatePret());
            date.add(GregorianCalendar.DAY_OF_YEAR, + 28);
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

    public String addLoan(ExemplaireLivre exemplaireLivre, Pret pretWithStatutPret, Reservation reservationByUtilisateur) throws FunctionalException {

        if (!exemplaireLivre.isDisponibilite() && pretWithStatutPret == null &&
                reservationByUtilisateur == null) {
            log.info("L'exemplaire '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible..." +
                    "\nMerci de faire une réservation.");
            throw new FunctionalException("Exemplaire non disponible.");

        } else if (exemplaireLivre.isDisponibilite() && pretWithStatutPret == null &&
                reservationByUtilisateur == null) {
            System.out.println("Ajout d'un nouveau prêt.");
            return Constantes.NOUVEAU_PRET;

        } else if (pretWithStatutPret == null && reservationByUtilisateur.getStatut().equals(Constantes.MIS_A_DISPO)) {
            return Constantes.MIS_A_DISPO;

        } else if (pretWithStatutPret != null) {
            log.info("Vous avez déjà un emprunt en cours sur ce livre.");
            throw new FunctionalException("Vous avez déjà un emprunt en cours sur ce livre.");

        } else if (reservationByUtilisateur != null) {
            log.info("Vous avez déjà une réservation en cours sur ce livre.");
            throw new FunctionalException("Vous avez déjà une réservation en cours sur ce livre.");
        }
        return null;
    }
}
