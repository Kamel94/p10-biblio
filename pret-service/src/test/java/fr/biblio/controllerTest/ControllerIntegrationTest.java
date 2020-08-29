package fr.biblio.controllerTest;

import fr.biblio.controller.PretController;
import fr.biblio.entities.Pret;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ControllerIntegrationTest {

    @Autowired
    private PretController controller;

    @Test
    public void getPretById() {
        // GIVEN & WHEN
        Pret pret = controller.getPret(60);

        // THEN
        assertThat(pret.getExemplaireId()).isEqualTo(2);
    }

    @Test
    public void getListPrets() {
        // GIVEN & WHEN
        List<Pret> prets = controller.listeDesPrets();

        // THEN
        assertThat(prets).isNotEmpty();
    }

}
