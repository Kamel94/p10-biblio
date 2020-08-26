package fr.biblio.service;

import fr.biblio.beans.ExemplaireLivre;
import fr.biblio.beans.LivreBean;
import fr.biblio.configuration.Constantes;
import fr.biblio.controller.PretController;
import fr.biblio.dao.PretRepository;
import fr.biblio.dao.ReservationRepository;
import fr.biblio.entities.Pret;
import fr.biblio.entities.Reservation;
import fr.biblio.exception.FunctionalException;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
public class ServicePretTest {

    private ServicePret service;
    @Mock
    private PretRepository pretrepository;
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
        pretrepository = mock(PretRepository.class, RETURNS_DEEP_STUBS);
        reservationRepository = mock(ReservationRepository.class, RETURNS_DEEP_STUBS);
    }

    @BeforeEach
    public void initPret() {
        service = new ServicePret();
        exemplaireLivre = new ExemplaireLivre();
        reservation = new Reservation();
        pret = new Pret();
        try {
            GregorianCalendar date = new GregorianCalendar();

            pret.setId(Long.valueOf(42));
            pret.setDatePret(new Date());
            date.setTime(pret.getDatePret());
            date.add(GregorianCalendar.DAY_OF_YEAR, + 28);
            pret.setUtilisateurId(Long.valueOf(1));
            pret.setDateRetour(date.getTime());
            pret.setProlongation(0);
            pret.setExemplaireId(Long.valueOf(1));
            pret.setStatut(Constantes.PRET);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkSavePret() {
        // GIVEN
        when(pretrepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(1), Long.valueOf(1), Constantes.PRET)).thenReturn(pret);

        // WHEN
        service.saveNewPret(pret.getUtilisateurId(), pret.getExemplaireId());

        // THEN
        assertTrue(pret.getId().equals(Long.valueOf(42)));
    }

    @Test
    public void given_Exemplaire_Is_Not_Available() throws FunctionalException {
        // GIVEN
        LivreBean livreBean = new LivreBean();
        livreBean.setId(Long.valueOf(1));
        livreBean.setTitre("Le livre de Java premier langage");
        exemplaireLivre.setId(Long.valueOf(1));
        exemplaireLivre.setBibliothequeId(Long.valueOf(1));
        exemplaireLivre.setLivreId(Long.valueOf(1));
        exemplaireLivre.setLivre(livreBean);
        exemplaireLivre.setDisponibilite(false);
        pret = null;
        reservation = null;
        when(pretrepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(1), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(null);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(Long.valueOf(2), exemplaireLivre.getId())).thenReturn(null);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () -> service.addLoan(exemplaireLivre, pret, reservation));

        // THEN
        assertTrue(exception.getMessage().equals("Exemplaire non disponible."));
    }

    @Test
    public void given_Exemplaire_Is_Available() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        exemplaireLivre.setDisponibilite(true);
        pret = null;
        reservation = null;
        when(pretrepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(2), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(null);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(Long.valueOf(2), exemplaireLivre.getId())).thenReturn(null);

        // WHEN
        String ajoutPret = service.addLoan(exemplaireLivre, pret, reservation);

        // THEN
        assertThat(ajoutPret).isEqualTo(Constantes.NOUVEAU_PRET);
    }

    @Test
    public void given_Pret_Is_Null_And_ReservationStatut_IsEqualsTo_MIS_A_DISPO() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        exemplaireLivre.setDisponibilite(true);
        pret = null;
        reservation.setUtilisateurId(Long.valueOf(2));
        reservation.setStatut(Constantes.MIS_A_DISPO);
        when(pretrepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(2), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(null);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(reservation.getUtilisateurId(), exemplaireLivre.getId())).thenReturn(reservation);

        // WHEN
        String misADispo = service.addLoan(exemplaireLivre, pret, reservation);

        // THEN
        assertThat(misADispo).isEqualTo(Constantes.MIS_A_DISPO);
    }

    @Test
    public void given_Pret_Is_Not_Null() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        exemplaireLivre.setDisponibilite(true);
        pret.setId(Long.valueOf(1));
        reservation = null;
        when(pretrepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(2), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(pret);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(Long.valueOf(2), exemplaireLivre.getId())).thenReturn(null);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () -> service.addLoan(exemplaireLivre, pret, reservation));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Vous avez déjà un emprunt en cours sur ce livre.");
    }

    @Test
    public void given_Reservation_Is_Not_Null() throws FunctionalException {
        // GIVEN
        exemplaireLivre.setId(Long.valueOf(1));
        reservation.setId(Long.valueOf(1));
        reservation.setStatut(Constantes.EN_ATTENTE);
        pret = null;
        when(pretrepository.findByUtilisateurIdAndExemplaireIdAndStatut(Long.valueOf(2), exemplaireLivre.getId(), Constantes.PRET)).thenReturn(null);
        when(reservationRepository.findByUtilisateurIdAndExemplaireId(Long.valueOf(2), exemplaireLivre.getId())).thenReturn(reservation);

        // WHEN
        FunctionalException exception = assertThrows(FunctionalException.class, () -> service.addLoan(exemplaireLivre, pret, reservation));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Vous avez déjà une réservation en cours sur ce livre.");
    }

}
