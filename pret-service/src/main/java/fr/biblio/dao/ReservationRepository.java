package fr.biblio.dao;

import fr.biblio.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByStatutAndExemplaireId(String statut, long exemplaireId);
    List<Reservation> findAllByUtilisateurId(long utilisateurId);
    List<Reservation> findAllByExemplaireId(long exemplaireId);
    Reservation findByUtilisateurIdAndExemplaireId(long utilisateurId, long exemplaireId);
}
