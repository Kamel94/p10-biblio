package fr.biblio.controllerTest;

import fr.biblio.configuration.Constantes;
import fr.biblio.controller.PretController;
import fr.biblio.entities.Pret;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PretController.class)
@AutoConfigureMockMvc
public class ControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PretController controller;

    private static Pret pret;

    @BeforeAll
    public static void init() {

        pret = new Pret();
        pret.setUtilisateurId(Long.valueOf(1));
        pret.setExemplaireId(Long.valueOf(1));
        pret.setDatePret(new Date());
        pret.setDateRetour(new Date());
        pret.setProlongation(0);
        pret.setStatut(Constantes.PRET);
    }

    @Test
    public void getPretById() throws Exception {
        // GIVEN
        when(controller.getPret(anyLong())).thenReturn(pret);

        // WHEN
        RequestBuilder request = MockMvcRequestBuilders.get("/prets/91");

        // THEN
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.jsonPath("$.utilisateurId").value(Long.valueOf(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exemplaireId").value(Long.valueOf(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statut").value("PRET"))
                .andExpect(status().isOk());

        verify(controller).getPret(anyLong());
    }

    @Test
    public void addPret() throws Exception {
        // GIVEN
        when(controller.addPret(pret.getUtilisateurId(), pret.getExemplaireId())).thenReturn(pret);

        // WHEN
        RequestBuilder request = MockMvcRequestBuilders.post("/ajoutPret/1/1");

        // THEN
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.jsonPath("$.utilisateurId").value(Long.valueOf(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exemplaireId").value(Long.valueOf(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statut").value("PRET"))
                .andExpect(status().isOk());

        verify(controller).addPret(pret.getUtilisateurId(), pret.getExemplaireId());
    }

    @Test
    public void delete() throws Exception {
        // GIVEN
        pret.setId(Long.valueOf(91));
        when(controller.delete(pret.getId())).thenReturn(null);

        // WHEN
        RequestBuilder request = MockMvcRequestBuilders.post("/delete/91");

        // THEN
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.utilisateurId").doesNotExist())
                .andExpect(status().isOk());

        verify(controller).delete(pret.getId());
    }
}
