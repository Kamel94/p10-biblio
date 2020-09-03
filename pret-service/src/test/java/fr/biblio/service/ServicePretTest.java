package fr.biblio.service;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.beans.LivreBean;
import fr.biblio.configuration.Constantes;
import fr.biblio.dao.PretRepository;
import fr.biblio.dao.ReservationRepository;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.exception.FunctionalException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ServicePretTest {

    @Mock
    private PretRepository pretRepository;
    @Mock
    private ReservationRepository reservationRepository;
    private ServicePret service;
    private ExemplaireLivre exemplaireLivre;
    private Reservation reservation;
    private static Pret pret;

    @AfterAll
    public static void assignToNull() {
        pret = null;
    }

    @BeforeEach
    public void initMock() {
        pretRepository = mock(PretRepository.class, RETURNS_DEEP_STUBS);
        reservationRepository = mock(ReservationRepository.class, RETURNS_DEEP_STUBS);
    }

    @BeforeEach
    public void init() {
        service = new ServicePret();
        exemplaireLivre = new ExemplaireLivre();
        reservation = new Reservation();
        pret = new Pret();
        try {
            GregorianCalendar date = new GregorianCalendar();

            pret.setId(Long.valueOf(42));
            pret.setDatePret(new Date());
            date.setTime(pret.getDatePret());
            date.add(GregorianCalendar.DAY_OF_YEAR, +28);
            pret.setDateRetour(date.getTime());
            pret.setUtilisateurId(Long.valueOf(1));
            pret.setProlongation(0);
            pret.setExemplaireId(Long.valueOf(1));
            pret.setStatut(Constantes.PRET);

        } catch (Exception e) {
            e.printStackTrace();
        }

        LivreBean livreBean = new LivreBean();
        livreBean.setId(Long.valueOf(1));
        livreBean.setTitre("Le livre de Java premier langage");

        reservation.setStatut(Constantes.EN_ATTENTE);
        reservation.setUtilisateurId(Long.valueOf(1));
        reservation.setExemplaireId(Long.valueOf(1));
        reservation.setBooking(new Date());
        exemplaireLivre.setId(Long.valueOf(1));
        exemplaireLivre.setBibliothequeId(Long.valueOf(1));
        exemplaireLivre.setLivreId(Long.valueOf(1));
        exemplaireLivre.setDisponibilite(false);
        exemplaireLivre.setLivre(livreBean);
    }

    @Test
    @DisplayName("Donne un prêt vide, ajoute le nouveau prêt et doit retourner le statut PRET.")
    public void checkSavePret() {
        // GIVEN
        Pret newPret = new Pret();
        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(1), Long.valueOf(1), Constantes.PRET)).thenReturn(null);

        // WHEN
        newPret = service.addNewPret(Long.valueOf(1), Long.valueOf(1));

        // THEN
        assertThat(newPret.getStatut()).isEqualTo(Constantes.PRET);
    }

    @Test
    @Tag("checkLoan")
    @DisplayName("Donne prêt et réservation non existants et l'exemplaire non disponible, vérifie les conditions du prêt et doit retourner une exception.")
    public void given_Exemplaire_When_Is_Not_Available_Then_FunctionalException() throws FunctionalException {
        // GIVEN
        pret = null;
        reservation = null;
        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(1), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(null);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(Long.valueOf(2), exemplaireLivre.getId())).thenReturn(null);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () -> service.checkLoan(exemplaireLivre, pret, reservation));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Exemplaire non disponible.");
    }

    @Test
    @Tag("checkLoan")
    @DisplayName("Donne prêt et réservation non existants et l'exemplaire disponible, vérifie les conditions du prêt et doit retourner le nombreExemplaire - 1.")
    public void given_Exemplaire_When_Is_Available_Then_NombreExemplaire_less_one() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        exemplaireLivre.setDisponibilite(true);
        exemplaireLivre.setNombreExemplaire(5);
        pret = null;
        reservation = null;
        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(2), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(null);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(Long.valueOf(2), exemplaireLivre.getId())).thenReturn(null);

        // WHEN
        String ajoutPret = service.checkLoan(exemplaireLivre, pret, reservation);

        // THEN
        assertThat(ajoutPret).isEqualTo(Constantes.NOUVEAU_PRET);
        assertThat(exemplaireLivre.getNombreExemplaire()).isEqualTo(4);
    }

    @Test
    @Tag("checkLoan")
    @DisplayName("Donne prêt non existant et le statut MIS A DISPO sur la réservation, vérifie les conditions du prêt et doit retourner le statut RECUPEREE sur la réservation.")
    public void given_Reservation_When_Statut_IsEqualsTo_MIS_A_DISPO_Then_Statut_IsEqualTo_RECUPEREE() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        exemplaireLivre.setDisponibilite(true);
        pret = null;
        reservation.setUtilisateurId(Long.valueOf(2));
        reservation.setStatut(Constantes.MIS_A_DISPO);
        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(2), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(null);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(reservation.getUtilisateurId(), exemplaireLivre.getId())).thenReturn(reservation);

        // WHEN
        String ajoutPret = service.checkLoan(exemplaireLivre, pret, reservation);

        // THEN
        assertThat(ajoutPret).isEqualTo(Constantes.NOUVEAU_PRET);
        assertThat(reservation.getStatut()).isEqualTo(Constantes.RECUPEREE);
    }

    @Test
    @Tag("checkLoan")
    @DisplayName("Donne prêt existant et réservation non existante, vérifie les conditions du prêt et doit retourner une exception.")
    public void given_Pret_When_Is_Not_Null_Then_FunctionalException() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        exemplaireLivre.setDisponibilite(true);
        pret.setId(Long.valueOf(1));
        reservation = null;
        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(2), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(pret);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(Long.valueOf(2), exemplaireLivre.getId())).thenReturn(null);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () -> service.checkLoan(exemplaireLivre, pret, reservation));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Vous avez déjà un emprunt en cours sur ce livre.");
    }

    @Test
    @Tag("checkLoan")
    @DisplayName("Donne prêt non existant et réservation existante, vérifie les conditions du prêt et doit retourner une exception.")
    public void given_Reservation_When_Is_Not_Null_Then_FunctionalException() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        reservation.setId(Long.valueOf(1));
        reservation.setStatut(Constantes.EN_ATTENTE);
        pret = null;
        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(2), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(null);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(Long.valueOf(2), exemplaireLivre.getId())).thenReturn(reservation);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () -> service.checkLoan(exemplaireLivre, pret, reservation));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Vous avez déjà une réservation en cours sur ce livre.");
    }

    @Test
    @Tag("extandLoan")
    @DisplayName("Donne un prêt avec prolongation est égale à 0 et dateRetour < today, prolonge le prêt et doit retourner prolongation est égale à 1.")
    public void given_Pret_When_Prolongation_IsEqualTo_0_And_DateRetour_Is_Before_Today_Then_ProlongationPret() {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(pret.getUtilisateurId(), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(pret);

        // WHEN
        service.extendLoan(pret);

        // THEN
        assertThat(pret.getProlongation()).isEqualTo(1);
    }

    @Test
    @Tag("extandLoan")
    @DisplayName("Donne un prêt avec prolongation est égale à 0 et dateRetour >= today, vérifie les condition de prolongation et doit retourner une exception.")
    public void given_Pret_When_Prolongation_IsEqualTo_0_And_DateRetour_Is_EqualTo_Or_After_Today_Then_FunctionalException() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        pret.setDateRetour(new Date());
        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(pret.getUtilisateurId(), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(pret);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () -> service.extendLoan(pret));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Vous ne pouvez plus prolonger ce prêt, car la date de retour du prêt est dépassée.");
    }

    @Test
    @Tag("extandLoan")
    @DisplayName("Donne un prêt avec prolongation est égale à 1, vérifie les condition de prolongation et doit retourner une exception.")
    public void given_Pret_When_Prolongation_IsEqualTo_1_Then_FunctionalException() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        pret.setProlongation(1);
        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(pret.getUtilisateurId(), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(pret);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () -> service.extendLoan(pret));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Ce prêt a atteint le nombre maximum de prolongation...");
    }

    @Test
    @Tag("returnBook")
    @DisplayName("Donne le statut PRET et une liste de réservation en attente non vide, vérifie les conditions du retour du prêt et " +
            "doit retourner le statut RENDU sur le prêt et le statut MIS A DISPO sur la réservation.")
    public void given_Pret_When_Staut_IsEqualTo_PRET_And_ReservationList_Is_Not_Empty_Then_StatutReservation_IsEqualTo_MIS_A_DISPO() {
        // GIVEN
        List<Reservation> reservationList = new ArrayList<>();

        reservationList.add(reservation);

        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(pret.getUtilisateurId(), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(pret);
        when(reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, exemplaireLivre.getId())).thenReturn(reservationList);

        // WHEN
        service.returnBook(pret, reservationList, exemplaireLivre);

        // THEN
        assertThat(pret.getStatut()).isEqualTo(Constantes.RENDU);
        assertThat(reservation.getStatut()).isEqualTo(Constantes.MIS_A_DISPO);
    }

    @Test
    @Tag("returnBook")
    @DisplayName("Donne le statut PRET et une liste de réservation en attente vide, vérifie les conditions du retour du prêt et " +
            "doit retourner le statut RENDU sur le prêt et ajouter 1 au nombre d'exemplaire.")
    public void given_Pret_When_Staut_IsEqualTo_PRET_And_ReservationList_Is_Empty_Then_NombreExemplaire_More_1() {
        // GIVEN
        List<Reservation> reservationList = new ArrayList<>();
        exemplaireLivre.setNombreExemplaire(9);

        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(pret.getUtilisateurId(), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(pret);

        // WHEN
        service.returnBook(pret, reservationList, exemplaireLivre);

        // THEN
        assertThat(pret.getStatut()).isEqualTo(Constantes.RENDU);
        assertThat(reservationList).isEmpty();
        assertThat(exemplaireLivre.getNombreExemplaire()).isEqualTo(10);
    }

    @Test
    @Tag("returnBook")
    @DisplayName("Donne le statut RENDU, vérifie les conditions du retour du prêt et doit retourner une exception.")
    public void given_Pret_When_Staut_Is_Not_EqualTo_PRET_Then_FunctionalException() throws FunctionalException {
        // GIVEN
        List<Reservation> reservationList = new ArrayList<>();
        pret.setStatut(Constantes.RENDU);

        when(pretRepository.findByUtilisateurIdAndExemplaireIdAndStatut(pret.getUtilisateurId(), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(pret);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () -> service.returnBook(pret, reservationList, exemplaireLivre));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Ce prêt n'est pas en cours...");
    }
}