package fr.biblio.service;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.beans.LivreBean;
import fr.biblio.configuration.Constantes;
import fr.biblio.dao.ReservationRepository;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.exception.FunctionalException;
import fr.biblio.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;
    private ReservationServiceImpl service;
    private ExemplaireLivre exemplaireLivre;
    private Reservation reservation;
    private static Pret pret;

    @AfterAll
    public static void assignToNull() {
        pret = null;
    }

    @BeforeEach
    public void initMock() {
        reservationRepository = mock(ReservationRepository.class, RETURNS_DEEP_STUBS);
    }

    @BeforeEach
    public void init() {
        service = new ReservationServiceImpl();
        reservation = new Reservation();
        pret = new Pret();
        exemplaireLivre = new ExemplaireLivre();
        LivreBean livreBean = new LivreBean();
        livreBean.setId(Long.valueOf(1));
        livreBean.setTitre("Le livre de Java premier langage");

        reservation.setId(Long.valueOf(15));
        reservation.setBooking(new Date());
        reservation.setExemplaireId(Long.valueOf(1));
        reservation.setUtilisateurId(Long.valueOf(1));
        reservation.setStatut(Constantes.EN_ATTENTE);
        reservation.setNotificationDate(null);

        exemplaireLivre.setId(Long.valueOf(1));
        exemplaireLivre.setBibliothequeId(Long.valueOf(1));
        exemplaireLivre.setLivreId(Long.valueOf(1));
        exemplaireLivre.setDisponibilite(true);
        exemplaireLivre.setNombreExemplaire(2);
        exemplaireLivre.setLivre(livreBean);

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
    }

    @Test
    @Tag("updateStatutOrNombreExemplaire")
    @DisplayName("Donne une réservation annulée et une liste de réservation en attente, vérifie si la liste est vide ou non et " +
            "doit retourner le statut MIS A DISPO pour le premier de la liste de réservation.")
    public void given_Reservation_When_Canceled_By_User_Then_Statut_Of_The_First_On_The_List_Is_EqualTo_MIS_A_DISPO() {
        // GIVEN
        List<Reservation> reservationList = new ArrayList<>();

        Reservation reservationMisADispo = new Reservation();
        reservationMisADispo.setBooking(new Date());
        reservationMisADispo.setExemplaireId(Long.valueOf(1));
        reservationMisADispo.setUtilisateurId(Long.valueOf(2));
        reservationMisADispo.setStatut(Constantes.EN_ATTENTE);

        Reservation reservationEnAttente = new Reservation();
        reservationEnAttente.setBooking(new Date());
        reservationEnAttente.setExemplaireId(Long.valueOf(1));
        reservationEnAttente.setUtilisateurId(Long.valueOf(2));
        reservationEnAttente.setStatut(Constantes.EN_ATTENTE);

        when(reservationRepository.findByUtilisateurIdAndExemplaireId(reservation.getUtilisateurId(), reservation.getExemplaireId())).thenReturn(reservation);
        reservationList.add(reservationMisADispo);
        reservationList.add(reservationEnAttente);

        // WHEN
        service.updateStatutOrNombreExemplaire(reservationList, exemplaireLivre);

        // THEN
        assertThat(reservationMisADispo.getStatut()).isEqualTo(Constantes.MIS_A_DISPO);
    }

    @Test
    @Tag("updateStatutOrNombreExemplaire")
    @DisplayName("Donne une réservation annulée et une liste de réservation en attente, vérifie si la liste est vide ou non et " +
            "doit retourner le statut EN ATTENTE pour le deuxième de la liste de réservation.")
    public void given_Reservation_When_Canceled_By_User_Then_Statut_Of_The_Second_On_The_List_Is_EqualTo_EN_ATTENTE() {
        // GIVEN
        List<Reservation> reservationList = new ArrayList<>();
        Reservation reservationMisADispo = new Reservation();
        reservationMisADispo.setBooking(new Date());
        reservationMisADispo.setExemplaireId(Long.valueOf(1));
        reservationMisADispo.setUtilisateurId(Long.valueOf(2));
        reservationMisADispo.setStatut(Constantes.EN_ATTENTE);

        Reservation reservationEnAttente = new Reservation();
        reservationEnAttente.setBooking(new Date());
        reservationEnAttente.setExemplaireId(Long.valueOf(1));
        reservationEnAttente.setUtilisateurId(Long.valueOf(2));
        reservationEnAttente.setStatut(Constantes.EN_ATTENTE);

        when(reservationRepository.findByUtilisateurIdAndExemplaireId(reservation.getUtilisateurId(), reservation.getExemplaireId())).thenReturn(reservation);
        reservationList.add(reservationMisADispo);
        reservationList.add(reservationEnAttente);

        // WHEN
        service.updateStatutOrNombreExemplaire(reservationList, exemplaireLivre);

        // THEN
        assertThat(reservationEnAttente.getStatut()).isEqualTo(Constantes.EN_ATTENTE);
    }

    @Test
    @Tag("updateStatutOrNombreExemplaire")
    @DisplayName("Donne une réservation annulée et une liste de réservation en attente, vérifie si la liste est vide ou non et " +
            "doit retourner le statut EN ATTENTE pour le deuxième de la liste de réservation.")
    public void given_Reservation_When_Canceled_By_User_And_ReservatioList_Is_Empty_Then_Add_One_In_NombreExemplaire() {
        // GIVEN
        List<Reservation> reservationList = new ArrayList<>();

        when(reservationRepository.findByUtilisateurIdAndExemplaireId(reservation.getUtilisateurId(), reservation.getExemplaireId())).thenReturn(reservation);

        // WHEN
        service.updateStatutOrNombreExemplaire(reservationList, exemplaireLivre);

        // THEN
        assertThat(reservationList).isEmpty();
        assertThat(exemplaireLivre.getNombreExemplaire()).isEqualTo(3);
    }

    @Test
    @Tag("addBookingService")
    @DisplayName("Donne nombreExemplaire > à la taille de la liste de réservation, vérifie les conditions d'ajout d'une réservation et doit retourner une nouvelle réservation.")
    public void given_Reservation_When_NombreExemplaire_Is_Better_Than_Size_Of_ReservationList_Then_New_Reservation() {
        // GIVEN
        List<Pret> prets = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        Date date = new Date();

        Reservation reservationEnAttente = new Reservation();
        reservationEnAttente.setBooking(date);
        reservationEnAttente.setExemplaireId(Long.valueOf(1));
        reservationEnAttente.setUtilisateurId(Long.valueOf(2));
        reservationEnAttente.setStatut(Constantes.EN_ATTENTE);

        reservationList.add(reservationEnAttente);

        Reservation newReservation = new Reservation();

        when(reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, Long.valueOf(1))).thenReturn(reservationList);

        // WHEN
        service.addBookingService(newReservation, exemplaireLivre, reservationList, prets, null, null, Long.valueOf(1));

        // THEN
        assertThat(newReservation.getStatut()).isEqualTo(Constantes.EN_ATTENTE);
        assertThat(newReservation.getBooking()).isEqualTo(newReservation.getBooking());
    }

    @Test
    @Tag("addBookingService")
    @DisplayName("Donne nombreExemplaire <= à la taille de la liste de réservation, vérifie les conditions d'ajout d'une réservation et doit retourner une exception.")
    public void given_Reservation_When_NombreExemplaire_Is_Less_Than_Or_Equal_To_Size_Of_ReservationList_Then_FunctionalException() {
        // GIVEN
        List<Pret> prets = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();

        Reservation reservationEnAttente = new Reservation();
        reservationEnAttente.setBooking(new Date());
        reservationEnAttente.setExemplaireId(Long.valueOf(1));
        reservationEnAttente.setUtilisateurId(Long.valueOf(2));
        reservationEnAttente.setStatut(Constantes.EN_ATTENTE);

        reservationList.add(reservationEnAttente);
        exemplaireLivre.setNombreExemplaire(0);

        Reservation newReservation = new Reservation();

        when(reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, Long.valueOf(1))).thenReturn(reservationList);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () ->
                service.addBookingService(newReservation, exemplaireLivre, reservationList, prets, null, null, Long.valueOf(1)));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Le livre '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible...");
    }

    @Test
    @Tag("addBookingService")
    @DisplayName("Donne prêt existant, vérifie les conditions d'ajout d'une réservation et doit retourner une exception.")
    public void given_Reservation_When_Pret_Is_Not_Null_Then_FunctionalException() {
        // GIVEN
        List<Pret> prets = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        Reservation newReservation = new Reservation();

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () ->
                service.addBookingService(newReservation, exemplaireLivre, reservationList, prets, null, pret, Long.valueOf(1)));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Vous avez déjà un emprunt en cours sur ce livre.");
    }

    @Test
    @Tag("addBookingService")
    @DisplayName("Donne réservation existante, vérifie les conditions d'ajout d'une réservation et doit retourner une exception.")
    public void given_Reservation_When_Reservation_Is_Not_Null_Then_FunctionalException() {
        // GIVEN
        List<Pret> prets = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        Reservation newReservation = new Reservation();

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () ->
                service.addBookingService(newReservation, exemplaireLivre, reservationList, prets, reservation, null, Long.valueOf(1)));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Vous avez déjà une réservation en cours sur ce livre.");
    }
}
