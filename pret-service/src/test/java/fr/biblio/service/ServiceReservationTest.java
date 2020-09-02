package fr.biblio.service;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.beans.LivreBean;
import fr.biblio.configuration.Constantes;
import fr.biblio.dao.ReservationRepository;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.exception.FunctionalException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class ServiceReservationTest {

    private ServiceReservation service;
    @Mock
    private ReservationRepository reservationRepository;
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
        service = new ServiceReservation();
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
    public void given_Reservation_When_NombreExemplaire_Is_Better_Than_Size_Of_ReservationList_Then_New_Reservation() {
        // GIVEN
        List<Pret> prets = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        Reservation reservation1 = new Reservation();
        reservation1.setBooking(new Date());
        reservation1.setExemplaireId(Long.valueOf(1));
        reservation1.setUtilisateurId(Long.valueOf(2));
        reservation1.setStatut(Constantes.EN_ATTENTE);
        reservationList.add(reservation1);

        Reservation reservation2 = new Reservation();

        when(reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, Long.valueOf(1))).thenReturn(reservationList);

        // WHEN
        service.addBookingService(reservation2, exemplaireLivre, reservationList, prets, null, null, Long.valueOf(1));

        // THEN
        assertThat(reservation.getStatut()).isEqualTo(Constantes.EN_ATTENTE);
    }

    @Test
    public void given_Reservation_When_NombreExemplaire_Is_Less_Than_Or_Equal_To_Size_Of_ReservationList_Then_FunctionalException() {
        // GIVEN
        List<Pret> prets = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        Reservation reservation1 = new Reservation();
        reservation1.setBooking(new Date());
        reservation1.setExemplaireId(Long.valueOf(1));
        reservation1.setUtilisateurId(Long.valueOf(2));
        reservation1.setStatut(Constantes.EN_ATTENTE);
        reservationList.add(reservation1);
        exemplaireLivre.setNombreExemplaire(0);

        Reservation reservation2 = new Reservation();

        when(reservationRepository.findAllByStatutAndExemplaireId(Constantes.EN_ATTENTE, Long.valueOf(1))).thenReturn(reservationList);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () ->
                service.addBookingService(reservation2, exemplaireLivre, reservationList, prets, null, null, Long.valueOf(1)));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Le livre '" + exemplaireLivre.getLivre().getTitre() + "' n'est pas disponible...");
    }

    @Test
    public void given_Reservation_When_Pret_Is_Not_Null_Then_FunctionalException() {
        // GIVEN
        List<Pret> prets = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        Reservation reservation1 = new Reservation();

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () ->
                service.addBookingService(reservation1, exemplaireLivre, reservationList, prets, null, pret, Long.valueOf(1)));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Vous avez déjà un emprunt en cours sur ce livre.");
    }

    @Test
    public void given_Reservation_When_Reservation_Is_Not_Null_Then_FunctionalException() {
        // GIVEN
        List<Pret> prets = new ArrayList<>();
        List<Reservation> reservationList = new ArrayList<>();
        Reservation reservation1 = new Reservation();

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () ->
                service.addBookingService(reservation1, exemplaireLivre, reservationList, prets, reservation, null, Long.valueOf(1)));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Vous avez déjà une réservation en cours sur ce livre.");
    }
}
