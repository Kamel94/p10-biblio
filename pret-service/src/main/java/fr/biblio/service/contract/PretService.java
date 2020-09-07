package fr.biblio.service.contract;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;

import java.util.Date;
import java.util.List;

public interface PretService {

    Pret addNewPret(long utilisateurId, long exemplaireId);
    String checkLoan(ExemplaireLivre exemplaireLivre, Pret pretWithStatutPret, Reservation reservationByUtilisateur);
    void extendLoan(Pret pret);
    void returnBook(Pret pret, List<Reservation> reservationList, ExemplaireLivre exemplaireLivre);
    List<Pret> findAll();
    List<Pret> findPretByStatut(String statut);
    List<Pret> findByUtilisateurIdAndStatut(long utilisateurId, String statut);
    List<Pret> findByStatutAndExemplaireId(String statut, long exemplaireId);
    List<Pret> findPretByStatutAndDateRetourBefore(String statut, Date date);
    List<Pret> findPretByStatutAndExemplaireIdOrderByDateRetourAsc(String statut, long exemplaireId);
    Pret save(Pret pret);
    Pret deleteById(long id);
    Pret findById(long id);
    Pret findByUtilisateurIdAndExemplaireIdAndStatut(long utilisateurId, long exemplaireId, String statut);
    Pret findByUtilisateurIdAndExemplaireIdAndStatutNotLike(long utilisateurId, long exemplaireId, String statut);
}
