package fr.biblio.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data @NoArgsConstructor
@AllArgsConstructor
@ToString
public class Reservation {

    private Long id;
    private Date booking;
    private Date notificationDate;
    private Long utilisateurId;
    private Long exemplaireId;
    private String statut;
    private boolean notification;

    public Reservation(Long id, Date booking, Date notificationDate, Long exemplaireId, Long utilisateurId, String statut, boolean notification) {
        this.id = id;
        this.booking = booking;
        this.notificationDate = notificationDate;
        this.statut = statut;
        this.notification = notification;
        this.exemplaireId = exemplaireId;
        this.utilisateurId = utilisateurId;
    }

    private String titreLivre;

}
