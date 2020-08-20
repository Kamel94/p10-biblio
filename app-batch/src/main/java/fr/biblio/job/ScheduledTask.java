package fr.biblio.job;

import fr.biblio.beans.*;
import fr.biblio.configuration.Constantes;
import fr.biblio.proxy.BatchProxy;
import fr.biblio.service.FormatDate;
import fr.biblio.service.SimpleEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ConditionalOnExpression("'${scheduler.enabled}'=='true'")
public class ScheduledTask {

    private final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private BatchProxy batchProxy;

    @Autowired
    private FormatDate formatDate;

    @Autowired
    private SimpleEmailService emailService;

    /**
     * Envoie automatiquement des mails aux utilisateurs qui n'ont pas rendu leurs prêts à temps
     * Si vous voulez programmer l'envoie pour tous les jours à 8h du matin,
     * vous devez mettre au niveau de cron "0 0 8 ? * *" .
     */
    @Scheduled(cron = "* * * ? * *", zone = "Europe/Paris")
    public void executeTask() {

        List<Pret> retourRetard = batchProxy.getPretsFinished();

        try {
            for (Pret pret : retourRetard) {
                Utilisateur utilisateur = batchProxy.getUtilisateur(pret.getUtilisateurId());
                ExemplaireLivre exemplaireLivre = batchProxy.getExemplaire(pret.getExemplaireId());
                Livre livre = batchProxy.getLivre(exemplaireLivre.getLivreId());
                Bibliotheque bibliotheque = batchProxy.getBibliotheque(exemplaireLivre.getBibliothequeId());

                String dateRetour = formatDate.patternDate(pret.getDateRetour());
                String civilite = "";
                String msgProlongement = "\nJe vous informe que malheureusement vous ne pouvez plus prolonger ce prêt.";
                String destinataire = utilisateur.getEmail();
                String objet = "Rappel, la date du prêt est arrivée à échéance !";

                if (utilisateur.getGenreId() == 1) {
                    civilite = "Mr";
                } else {
                    civilite = "Mme";
                }

                String message = "Bonjour " + civilite + " " + utilisateur.getNom() + "," +
                        "\n\nLa date de retour pour le livre " + "''" + livre.getTitre() + "''" +
                        " de " + livre.getAuteur() +
                        " était le " + dateRetour + "..." +
                        "\nMerci de rapporter le livre au plus tôt à la bibliothèque " +
                        "''" + bibliotheque.getNom() + "''" + "." +
                        "\n" + msgProlongement +
                        "\n\nService de la ville";

                // envoie du mail
                log.info("****************************************************************************************");
                log.info("Rappel envoyé a: " + destinataire);
                log.info("****************************************************************************************");
                emailService.sendSimpleEmail(destinataire, objet, message);
            }
            if (retourRetard.isEmpty()) {
                log.info("****************************************************************************************");
                log.info("Il n'y a aucun email de rappel à envoyer.");
                log.info("****************************************************************************************");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie automatiquement des mails aux utilisateurs pour les prévenir de la mis à disposition du livre qu'ils ont réservé.
     * Si vous voulez programmer l'envoie pour tous les jours à 8h du matin,
     * vous devez mettre au niveau de cron "0 0 8 ? * *" .
     */
    @Scheduled(cron = "* * * ? * *", zone = "Europe/Paris")
    public void executeTask2() {

        List<Reservation> reservationList = batchProxy.getReservationListByStatutAndNotification(Constantes.MIS_A_DISPO, false);

        try {
            for (Reservation reservation : reservationList) {
                Utilisateur utilisateur = batchProxy.getUtilisateur(reservation.getUtilisateurId());
                ExemplaireLivre exemplaireLivre = batchProxy.getExemplaire(reservation.getExemplaireId());
                Livre livre = batchProxy.getLivre(exemplaireLivre.getLivreId());
                Bibliotheque bibliotheque = batchProxy.getBibliotheque(exemplaireLivre.getBibliothequeId());

                String booking = formatDate.patternDate(reservation.getBooking());
                String civilite = "";
                String destinataire = utilisateur.getEmail();
                String objet = "Mis à disposition !";

                batchProxy.updateReservation(reservation.getId());

                if (utilisateur.getGenreId() == 1) {
                    civilite = "Mr";
                } else {
                    civilite = "Mme";
                }

                String message = "Bonjour " + civilite + " " + utilisateur.getNom() + "," +
                        "\n\nLe livre " + "''" + livre.getTitre() + "''" +
                        " de " + livre.getAuteur() +
                        " que vous avez réservé le " + booking + " est de nouveau disponible." +
                        "\nMerci de venir le chercher au plus tôt à la bibliothèque " +
                        "''" + bibliotheque.getNom() + "''" + "." +
                        "\n" + "Attention ! Vous disposez de 48h pour le récupérer, passé ce délai, il ne vous sera plus réservé." +
                        "\n\nService de la ville";

                // envoie du mail
                log.info("****************************************************************************************");
                log.info("Email envoyé a: " + destinataire);
                log.info("****************************************************************************************");
                emailService.sendSimpleEmail(destinataire, objet, message);
            }

            if (reservationList.isEmpty()) {
                log.info("****************************************************************************************");
                log.info("Il n'y a aucun email de mis à disposition à envoyer.");
                log.info("****************************************************************************************");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
