package fr.biblio.proxies;

import fr.biblio.beans.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "zuul-server", url = "localhost:8888")
public interface WebProxy {

    @GetMapping(value = "/livre-service/livres")
    List<Livre> listeDesLivres();

    @GetMapping(value = "/livre-service/livres/{id}")
    Livre getLivre(@PathVariable(name = "id") long id);

    @GetMapping(value = "/livre-service/chercherLivre")
    List<Livre> chercherLivreParCriteres(@RequestParam(name="titre", defaultValue = "") String titre,
                              @RequestParam(name="auteur", defaultValue = "") String auteur,
                              @RequestParam(name="categorie", defaultValue = "") String categorie);

    @GetMapping(value = "/livre-service/listExemplaireLivres/{livreId}")
    List<ExemplaireLivre> getListExemplairesWithLivreId(@PathVariable("livreId") long id);

    @GetMapping("/livre-service/exemplaireLivres/{livreId}")
    ExemplaireLivre getExemplaireWithLivreId(@PathVariable("livreId") long id);

    @GetMapping("/livre-service/exemplaireLivre/{id}")
    ExemplaireLivre getExemplaire(@PathVariable("id") long id);

    @GetMapping(value = "/authentification-service/listeUtilisateurs")
    List<Utilisateur> listeUtilisateurs();

    @GetMapping(value = "/authentification-service/utilisateur/{id}")
    Utilisateur getUtilisateur(@PathVariable("id") long id);

    @GetMapping(value = "/authentification-service/utilisateurByEmail/{email}")
    Utilisateur getUtilisateurWithEmail(@PathVariable("email") String email);

    @GetMapping(value = "/authentification-service/utilisateurByPseudo/{pseudo}")
    Utilisateur getUtilisateurWithPseudo(@PathVariable("pseudo") String pseudo);

    @PostMapping(value = "/authentification-service/ajoutUtilisateur")
    Utilisateur ajouterUtilisateur(@RequestBody Utilisateur utilisateur);

    @GetMapping(value = "/pret-service/prets/{id}")
    Pret getPret(@PathVariable("id") long id);

    @GetMapping(value = "/pret-service/prets/{utilisateurId}/{exemplaireId}/{statut}")
    Pret getPretWithUtilisateurIdAndExemplaireIdAndStatut(@PathVariable("utilisateurId") long utilisateurId,
                                                                 @PathVariable("exemplaireId") long exemplaireId,
                                                                 @PathVariable("statut") String statut);

    @GetMapping(value = "/pret-service/prets/{utilisateurId}/{exemplaireId}/notLike/{statut}")
    Pret findByUtilisateurIdAndExemplaireIdAndStatutNotLike(@PathVariable("utilisateurId") long utilisateurId,
                                                 @PathVariable("exemplaireId") long exemplaireId,
                                                 @PathVariable("statut") String statut);

    @RequestMapping(value = "/pret-service/prets")
    List<Pret> listeDesPrets();

    @GetMapping(value = "/pret-service/pretsByStatut/{statut}")
    List<Pret> getPretsByStatut(@PathVariable("statut") String statut);

    @GetMapping(value = "/pret-service/pretUtilisateur/{utilisateurId}")
    List<Pret> getPretsWithUtilisateurId(@PathVariable("utilisateurId") long utilisateurId);

    @GetMapping(value = "/pret-service/pretUtilisateur/{utilisateurId}/{statut}")
    List<Pret> getPretsWithUtilisateurIdAndStatut(@PathVariable("utilisateurId") long utilisateurId, @PathVariable("statut") String statut);

    @GetMapping(value = "/pret-service/pretsWithStatutPretAndExemplaireId/{exemplaireId}")
    List<Pret> getPretsWithStatutPretAndExemplaireId(@PathVariable("exemplaireId") long exemplaireId);

    @GetMapping(value = "/pret-service/pretsOrderByDateRetourAsc/{exemplaireId}")
    List<Pret> getPretsOrderByDateRetourAsc(@PathVariable("exemplaireId") long exemplaireId);

    @GetMapping(value = "/pret-service/pretsWithStatutAndExemplaireId/{statut}/{exemplaireId}")
    List<Pret> getPretsWithStatutAndExemplaireId(@PathVariable("statut") String statut, @PathVariable("exemplaireId") long exemplaireId);

    @PostMapping(value = "/pret-service/ajoutPret/{utilisateurId}/{exemplaireId}")
    Pret addPret(@PathVariable("utilisateurId") long utilisateurId,
                 @PathVariable("exemplaireId") long exemplaireId);

    @PostMapping(value = "/pret-service/prolongation/{pretId}")
    Pret prolongerPret(@PathVariable("pretId") long pretId);

    @PostMapping(value = "/pret-service/delete/{id}")
    Pret delete(@PathVariable("id") long id);

    @GetMapping(value = "/pret-service/reservations")
    List<Reservation> getReservationList();

    @GetMapping(value = "/pret-service/reservationsByUtilisateurId/{utilisateurId}")
    List<Reservation> getReservationListByUtilisateurId(@PathVariable("utilisateurId") long utilisateurId);

    @GetMapping(value = "/pret-service/reservationsByExemplaireId/{exemplaireId}")
    List<Reservation> getReservationListByExemplaireId(@PathVariable("exemplaireId") long exemplaireId);

    @GetMapping(value = "/pret-service/reservationsByStatutNotLikeAndExemplaireId/{statut}/{exemplaireId}")
    List<Reservation> getReservationListByStatutNotLikeAndExemplaireId(@PathVariable("statut") String statut,
                                                                @PathVariable("exemplaireId") long exemplaireId);

    @GetMapping(value = "/pret-service/reservationByUtilisateurIdAndExemplaireId/{utilisateurId}/{exemplaireId}")
    Reservation getReservationByUtilisateurIdAndExemplaireId(@PathVariable("utilisateurId") long utilisateurId,
                                                             @PathVariable("exemplaireId") long exemplaireId);

    @PostMapping(value = "/pret-service/ajoutReservation/{utilisateurId}/{exemplaireId}")
    Reservation addBooking(@PathVariable("utilisateurId") long utilisateurId,
                           @PathVariable("exemplaireId") long exemplaireId);

}
