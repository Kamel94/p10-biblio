package fr.biblio.controller;

import fr.biblio.beans.*;
import fr.biblio.proxies.WebProxy;
import fr.biblio.service.CompteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
public class ApplicationController {

    @Autowired
    private WebProxy webProxy;

    @Autowired
    private CompteService compteService;

    Logger log = LoggerFactory.getLogger(ApplicationController.class);

    @GetMapping("/accueil")
    public String accueil(Model model, Principal principal,
                          @RequestParam(name = "titre", defaultValue = "") String titre,
                          @RequestParam(name = "auteur", defaultValue = "") String auteur,
                          @RequestParam(name = "categorie", defaultValue = "") String categorie) {


        List<Livre> livres = webProxy.chercherLivreParCriteres(titre, auteur, categorie);

        if (principal != null) {
            Utilisateur utilisateur = webProxy.getUtilisateurWithPseudo(principal.getName());
            model.addAttribute("utilisateur", utilisateur);
        } else {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(Long.valueOf(0));
            model.addAttribute("utilisateur", utilisateur);
        }

        model.addAttribute("titre", titre);
        model.addAttribute("auteur", auteur);
        model.addAttribute("categorie", categorie);
        model.addAttribute("livres", livres);

        return "accueil";
    }

    /**
     * Affiche le détail d'un livre.
     */
    @GetMapping("/detailsLivre/{id}")
    public String detailsLivre(@PathVariable("id") long id, Model model, Principal principal) {

        Livre livre = webProxy.getLivre(id);
        ExemplaireLivre exemplaire = webProxy.getExemplaireWithLivreId(id);
        List<Pret> prets = webProxy.getPretsWithStatutPretAndExemplaireId(exemplaire.getId());
        List<Pret> pretsOrderByDate = webProxy.getPretsOrderByDateRetourAsc(exemplaire.getId());
        List<Reservation> reservationList = webProxy.getReservationList();

        String formatDate = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
        String date = simpleDateFormat.format(livre.getEdition());
        livre.setEditionString(date);
        int nombreExemplaire = (exemplaire.getNombreExemplaire() + prets.size()) * 2;

        if (principal == null) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(Long.valueOf(0));
            model.addAttribute("utilisateur", utilisateur);
        } else {
            Utilisateur utilisateur = webProxy.getUtilisateurWithPseudo(principal.getName());
            Pret pret = webProxy.findByUtilisateurIdAndExemplaireIdAndStatutNotLike(utilisateur.getId(), exemplaire.getId(), "RENDU");

            if (pret == null) {
                Pret newPret = new Pret();
                newPret.setUtilisateurId(Long.valueOf(0));
                model.addAttribute("p", newPret);
            } else {
                model.addAttribute("p", pret);
            }
            model.addAttribute("utilisateur", utilisateur);
        }

        if (reservationList.size() != 0) {
            int sizeListreservation = reservationList.size();
            model.addAttribute("reservationList", reservationList);
            model.addAttribute("sizeListreservation", sizeListreservation);
        } else {
            Reservation reservation = new Reservation(Long.valueOf(0), new Date(), new Date(), Long.valueOf(0), Long.valueOf(0), null, false);
            reservationList.add(reservation);
            int sizeListreservation = reservationList.size() - 1;
            model.addAttribute("reservationList", reservationList);
            model.addAttribute("sizeListreservation", sizeListreservation);
        }

        if (pretsOrderByDate.size() != 0) {
            String dateRetour = simpleDateFormat.format(pretsOrderByDate.get(0).getDateRetour());
            int sizeList = pretsOrderByDate.size();
            pretsOrderByDate.get(0).setDateRetourString(dateRetour);
            model.addAttribute("pretsOrderByDate", pretsOrderByDate);
            model.addAttribute("sizeList", sizeList);
        } else {
            Pret pret1 = new Pret(Long.valueOf(0), new Date(), new Date(), null, 0, Long.valueOf(0), Long.valueOf(0));
            pretsOrderByDate.add(pret1);
            int sizeList = pretsOrderByDate.size() - 1;
            pretsOrderByDate.get(0).setDateRetourString("//");
            model.addAttribute("pretsOrderByDate", pretsOrderByDate);
            model.addAttribute("sizeList", sizeList);
        }

        model.addAttribute("nombreExemplaire", nombreExemplaire);
        model.addAttribute("prets", prets);
        model.addAttribute("exemplaire", exemplaire);
        model.addAttribute("livre", livre);
        model.addAttribute("pret", new Pret());
        model.addAttribute("livre", livre);
        model.addAttribute("localDate", LocalDate.now());

        return "details";
    }

    /**
     * Affiche la liste des prêts en cours de l'utilisateur connecté.
     */
    @GetMapping(value = "/usager/pretUtilisateur/{utilisateurId}")
    public String pretUtilisateur(Model model,
                                  @PathVariable("utilisateurId") long utilisateurId) {

        List<Pret> prets = webProxy.getPretsWithUtilisateurId(utilisateurId);

        Utilisateur utilisateur = webProxy.getUtilisateur(utilisateurId);

        String formatDate = "dd/MM/yyyy";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);

        try {
            for (Pret pret : prets) {
                String date = simpleDateFormat.format(pret.getDateRetour());
                pret.setDateRetourString(date);
                model.addAttribute("date", date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("today", new Date());
        model.addAttribute("prets", prets);
        model.addAttribute("utilisateur", utilisateur);

        return "pretUtilisateur";
    }

    /**
     * Affiche la liste des réservations en cours de l'utilisateur connecté.
     */
    @GetMapping(value = "/usager/reservationsUtilisateur/{utilisateurId}")
    public String reservationUtilisateur(Model model,
                                         @PathVariable("utilisateurId") long utilisateurId) {

        List<Reservation> reservationList = webProxy.getReservationListByUtilisateurId(utilisateurId);
        Utilisateur utilisateur = webProxy.getUtilisateur(utilisateurId);

        String formatDate = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);

        try {
            for (Reservation reservation : reservationList) {
                List<Pret> pretsOrderByDate = webProxy.getPretsOrderByDateRetourAsc(reservation.getExemplaireId());
                ExemplaireLivre exemplaireLivre = webProxy.getExemplaire(reservation.getExemplaireId());
                Livre livre = webProxy.getLivre(exemplaireLivre.getLivreId());

                if (reservationList.size() != 0) {
                    int sizeListreservation = reservationList.size();
                    model.addAttribute("reservationList", reservationList);
                    model.addAttribute("sizeListreservation", sizeListreservation);
                } else {
                    Reservation resa = new Reservation(Long.valueOf(0), new Date(), new Date(), Long.valueOf(0), Long.valueOf(0), null, false);
                    reservationList.add(resa);
                    int sizeListreservation = reservationList.size() - 1;
                    model.addAttribute("reservationList", reservationList);
                    model.addAttribute("sizeListreservation", sizeListreservation);
                }

                if (pretsOrderByDate.size() != 0) {
                    String dateRetour = simpleDateFormat.format(pretsOrderByDate.get(0).getDateRetour());
                    int sizeList = pretsOrderByDate.size();
                    pretsOrderByDate.get(0).setDateRetourString(dateRetour);
                    model.addAttribute("pretsOrderByDate", pretsOrderByDate);
                    model.addAttribute("sizeList", sizeList);
                } else {
                    Pret pret1 = new Pret(Long.valueOf(0), new Date(), new Date(), null, 0, Long.valueOf(0), Long.valueOf(0));
                    pretsOrderByDate.add(pret1);
                    int sizeList = pretsOrderByDate.size() - 1;
                    pretsOrderByDate.get(0).setDateRetourString("//");
                    model.addAttribute("pretsOrderByDate", pretsOrderByDate);
                    model.addAttribute("sizeList", sizeList);
                }
                reservation.setTitreLivre(livre.getTitre());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("today", new Date());
        model.addAttribute("utilisateur", utilisateur);

        return "reservationsUtilisateur";
    }

    /**
     * Affiche les informations de la réservation en cours de l'utilisateur connecté.
     */
    @GetMapping(value = "/usager/reservationsUtilisateur/information/{utilisateurId}/{exemplaireId}")
    public String infosReservation(Model model, @PathVariable("utilisateurId") long utilisateurId,
                                   @PathVariable("exemplaireId") long exemplaireId) {

        String formatDate = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatDate);
        List<Pret> pretsOrderByDate = webProxy.getPretsOrderByDateRetourAsc(exemplaireId);
        List<Reservation> reservationList = webProxy.getReservationListByExemplaireId(exemplaireId);
        ExemplaireLivre exemplaireLivre = webProxy.getExemplaire(exemplaireId);
        Livre livre = webProxy.getLivre(exemplaireLivre.getLivreId());
        Reservation reservation = webProxy.getReservationByUtilisateurIdAndExemplaireId(utilisateurId, exemplaireId);

        if (reservationList.size() != 0) {
            model.addAttribute("reservationList", reservationList);
        } else {
            Reservation resa = new Reservation(Long.valueOf(0), new Date(), new Date(), Long.valueOf(0), Long.valueOf(0), null, false);
            reservationList.add(resa);
            model.addAttribute("reservationList", reservationList);
        }

        if (pretsOrderByDate.size() != 0) {
            String dateRetour = simpleDateFormat.format(pretsOrderByDate.get(0).getDateRetour());
            pretsOrderByDate.get(0).setDateRetourString(dateRetour);
            model.addAttribute("pretsOrderByDate", pretsOrderByDate);
        } else {
            Pret pret1 = new Pret(Long.valueOf(0), new Date(), new Date(), null, 0, Long.valueOf(0), Long.valueOf(0));
            pretsOrderByDate.add(pret1);
            pretsOrderByDate.get(0).setDateRetourString("//");
            model.addAttribute("pretsOrderByDate", pretsOrderByDate);
        }
        model.addAttribute("livre", livre);
        model.addAttribute("reservation", reservation);

        return "infosReservation";
    }

    /**
     * Permet aux usagers de prolonger un emprunt.
     */
    @PostMapping(value = "/prolongation/{pretId}/{utilisateurId}")
    public String prolongation(@PathVariable("pretId") long pretId,
                               @PathVariable("utilisateurId") long utilisateurId) {

        Pret prolongation = webProxy.prolongerPret(pretId);

        return "redirect:/usager/pretUtilisateur/{utilisateurId}";
    }

    /**
     * Permet aux usagers de demander un emprunt.
     */
    @GetMapping(value = "/usager/ajoutReservation/{utilisateurId}/{exemplaireId}/{livreId}")
    public String addBooking(@PathVariable("exemplaireId") long exemplaireId,
                          @PathVariable("utilisateurId") long utilisateurId,
                          @PathVariable("livreId") long livreId) {

        webProxy.addBooking(utilisateurId, exemplaireId);

        return "redirect:/detailsLivre/{livreId}";
    }

    /**
     * Affiche le formulaire d'inscription.
     */
    @GetMapping(value = "/inscription")
    public String inscription(Model model, Principal principal) {

        LocalDateTime dateTime = LocalDateTime.now();

        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("localDate", dateTime);

        if (principal != null) {
            Utilisateur utilisateur = webProxy.getUtilisateurWithPseudo(principal.getName());
            model.addAttribute("u", utilisateur);
        } else {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setStatut("VISITEUR");
            model.addAttribute("u", utilisateur);
        }

        return "inscription";
    }

    /**
     * Méthode qui permet d'enregistrer l'inscription ou
     * de renvoyer vers le formulaire d'inscription en cas d'erreur.
     */
    @RequestMapping(value = "/enregistrer", method = RequestMethod.POST)
    public String enregistrer(Model model, @Valid Utilisateur utilisateur, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Erreur lors de l'inscription " + bindingResult.getFieldError());
            return "redirect:/inscription";
        }

        log.info("Utilisateur ajouté");

        compteService.saveUser(utilisateur);
        return "confirmation";
    }

    /**
     * Supprime un prêt.
     */
    @GetMapping(value = "/delete/{id}/{utilisateurId}")
    public String delete(@PathVariable("id") long id, @PathVariable("utilisateurId") long utilisateurId){
        webProxy.delete(id);
        log.info("Le prêt avec l'id " + id + " a été supprimé");
        return "redirect:/usager/reservationsUtilisateur/{utilisateurId}";
    }
}
