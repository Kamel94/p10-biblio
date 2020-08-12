package fr.biblio.controller;

import fr.biblio.beans.Bibliotheque;
import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.beans.LivreBean;
import fr.biblio.configuration.Constantes;
import fr.biblio.exception.FunctionalException;
import fr.biblio.proxies.PretProxy;
import fr.biblio.dao.PretRepository;
import fr.biblio.entities.Pret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@RestController
public class PretController {

    @Autowired
    private PretRepository pretRepository;

    @Autowired
    private PretProxy pretProxy;

    Logger log = LoggerFactory.getLogger(PretController.class);

    /**
     * Affiche la liste des prêts.
     */
    @GetMapping(value = "/prets")
    public List<Pret> listeDesPrets() {
        return pretRepository.findAll();
    }

    /**
     * Affiche un prêt de par son ID.
     */
    @GetMapping(value = "/prets/{id}")
    public Pret getPret(@PathVariable("id") long id) {
        return pretRepository.findById(id).get();
    }

    /**
     * Affiche la liste des prêts d'un utilisateur.
     */
    @GetMapping(value = "/pretUtilisateur/{utilisateurId}")
    public List<Pret> getPretsWithUtilisateurId(@PathVariable("utilisateurId") long utilisateurId) {

        List<Pret> prets = pretRepository.findByUtilisateurIdAndStatut(utilisateurId, Constantes.PRET);
        for (Pret pret : prets) {
            ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(pret.getExemplaireId());
            LivreBean livre = pretProxy.getLivre(exemplaireLivre.getLivreId());
            Bibliotheque bibliotheque = pretProxy.getBibliotheque(exemplaireLivre.getBibliothequeId());

            pret.setTitreLivre(livre.getTitre());
            pret.setNumeroSerieExemplaire(exemplaireLivre.getNumeroSerie());
            pret.setNomBiblio(bibliotheque.getNom());
        }
        return prets;
    }

    /**
     * Affiche la liste des prêts dont la date du prêt est passé.
     */
    @GetMapping(value = "/dateRetourPassee")
    public List<Pret> getPretsFinished() {
        Date date = new Date();
        List<Pret> prets = pretRepository.findPretByStatutAndDateRetourBefore(Constantes.PRET, date);
        return prets;
    }

    /**
     * Permet de modifier le statut et la date retour une fois le livre rendu.
     */
    @PostMapping(value = "/livreRendu/{pretId}")
    public Pret rendreLivre(@PathVariable("pretId") long pretId) {

        List<Pret> prets = pretRepository.findPretByStatut(Constantes.EN_ATTENTE);
        Pret pret = pretRepository.findById(pretId).get();
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(pret.getExemplaireId());
        int i = 0;

        if (pret.getStatut().equals(Constantes.PRET) || pret.getStatut().equals(Constantes.MIS_A_DISPO)) {

            if (prets.isEmpty()) {
                exemplaireLivre.setNombreExemplaire(exemplaireLivre.getNombreExemplaire() + 1);
            }

            if (exemplaireLivre.getNombreExemplaire() > 0) {
                exemplaireLivre.setDisponibilite(true);
            }

            pretProxy.updateExemplaire(exemplaireLivre);
            pret.setStatut(Constantes.RENDU);
            pret.setDateRetour(new Date());

                if (!prets.isEmpty() && pret.getExemplaireId() == prets.get(i).getExemplaireId()) {
                    try {
                        GregorianCalendar date = new GregorianCalendar();

                        date.setTime(prets.get(i).getDatePret());
                        date.add(GregorianCalendar.DAY_OF_YEAR, + 28);

                        prets.get(i).setStatut(Constantes.MIS_A_DISPO);
                        prets.get(i).setDateRetour(date.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return pretRepository.save(prets.get(i));
                }

        } else {
            log.info("Ce prêt n'est pas en cours...");
        }
        return pretRepository.save(pret);
    }

    /**
     * Permet de prolonger un prêt.
     */
    @PostMapping(value = "/prolongation/{pretId}")
    public Pret prolongerPret(@PathVariable("pretId") long pretId) {

        Pret pret = pretRepository.findById(pretId).get();
        Date today = new Date();

        long todayLong = today.getTime();
        long dateRetourLong = pret.getDateRetour().getTime();

        if (pret.getProlongation() == 0 && todayLong < dateRetourLong) {
            try {
                GregorianCalendar date = new GregorianCalendar();

                date.setTime(pret.getDateRetour());
                date.add(GregorianCalendar.DAY_OF_YEAR, + 28);

                pret.setDateRetour(date.getTime());
                pret.setProlongation(pret.getProlongation() + 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return pretRepository.save(pret);

        } else if (pret.getProlongation() == 0 && todayLong >= dateRetourLong) {
            log.info("Vous ne pouvez plus prolonger ce prêt, car la date de retour du prêt est dépassée.");
            throw new FunctionalException("Vous ne pouvez plus prolonger ce prêt, car la date de retour du prêt est dépassée.");

        } else if (pret.getProlongation() >= 1) {
            log.info("Ce prêt a atteint le nombre maximum de prolongation...");
            throw new FunctionalException("Ce prêt a atteint le nombre maximum de prolongation...");
        }
        return null;
    }

    /**
     * Permet d'enregistrer un prêt.
     */
    @PostMapping(value = "/ajoutPret/{utilisateurId}/{exemplaireId}")
    public Pret addPret(@PathVariable("utilisateurId") long utilisateurId,
                          @PathVariable("exemplaireId") long exemplaireId) {

        Pret pret = new Pret();
        List<Pret> prets = pretRepository.findPretByStatut(Constantes.EN_ATTENTE);
        ExemplaireLivre exemplaireLivre = pretProxy.getExemplaire(exemplaireId);
        List<Pret> pretsByExplaireId = pretRepository.findByStatutAndExemplaireId(Constantes.PRET, exemplaireId);

        int nombreExemplaire = exemplaireLivre.getNombreExemplaire() + pretsByExplaireId.size();
        System.out.println(nombreExemplaire);
        System.out.println(prets.size());
        if(!exemplaireLivre.isDisponibilite() && nombreExemplaire * 2 > prets.size()) {
            log.info("L'exemplaire '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible..." +
                    "\nOn vous préviendra une fois qu'il sera de nouveau disponible.");
            pret.setDatePret(new Date());
            pret.setUtilisateurId(utilisateurId);
            pret.setDateRetour(new Date());
            pret.setProlongation(0);
            pret.setExemplaireId(exemplaireLivre.getId());
            pret.setStatut(Constantes.EN_ATTENTE);

        } else if (nombreExemplaire * 2 <= prets.size()) {
            log.info("L'exemplaire '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible...");
            System.out.println(nombreExemplaire);
            return null;
        } else {
            exemplaireLivre.setNombreExemplaire(exemplaireLivre.getNombreExemplaire() - 1);

            if (exemplaireLivre.getNombreExemplaire() == 0) {
                exemplaireLivre.setDisponibilite(false);
            }

            pretProxy.updateExemplaire(exemplaireLivre);

            try {
                GregorianCalendar date = new GregorianCalendar();

                pret.setDatePret(new Date());

                date.setTime(pret.getDatePret());
                date.add(GregorianCalendar.DAY_OF_YEAR, +28);

                pret.setUtilisateurId(utilisateurId);
                pret.setDateRetour(date.getTime());
                pret.setProlongation(0);
                pret.setExemplaireId(exemplaireLivre.getId());
                pret.setStatut(Constantes.PRET);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pretRepository.save(pret);
    }

}
