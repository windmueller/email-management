package de.freewarepoint.interviews.email.controllers;

import de.freewarepoint.interviews.email.EmailRepository;
import de.freewarepoint.interviews.email.entities.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class EmailRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmailRepository repository;

    @BeforeEach
    void clearRepository() {
        repository.deleteAll();
    }

    @Test
    void batchQuery() throws Exception {
        // Given
        var firstMail = new Email();
        firstMail.setFrom("vir.cotto@centauri.gov");
        firstMail = repository.save(firstMail);
        var secondMail = new Email();
        secondMail.setFrom("jeffrey.sinclair@earth-alliance.org");
        secondMail = repository.save(secondMail);
        assertThat(repository.count()).isEqualTo(2);

        // When
        mvc.perform(get("/emails?ids={first},{second}", firstMail.getId(), secondMail.getId()))
                .andExpect(status().isOk()); // Then
    }

    @Test
    void batchInsert() throws Exception {
        // Given
        assertThat(repository.count()).isEqualTo(0);

        // When
        mvc.perform(post("/emails/batch-insert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                """
                [
                  {
                    "from": "stephen.franklin@earth-alliance.org",
                    "to": ["john.sheridan@earth-alliance.org"],
                    "subject": "New medical equipment required"
                  },
                  {
                    "from": "michael.garibaldi@earth-alliance.org",
                    "to": ["zack.allan@earth-alliance.org"],
                    "subject": "Improve security in brown sector"
                  }
                ]
                """))
                .andExpect(status().isOk());

        // Then
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void batchDelete() throws Exception {
        // Given
        var firstMail = new Email();
        firstMail.setFrom("vir.cotto@centauri.gov");
        firstMail = repository.save(firstMail);
        var secondMail = new Email();
        secondMail.setFrom("jeffrey.sinclair@earth-alliance.org");
        secondMail = repository.save(secondMail);
        assertThat(repository.count()).isEqualTo(2);

        final var idList = "[ " + firstMail.getId() + ", " + secondMail.getId() + " ]";

        // When
        mvc.perform(post("/emails/batch-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(idList))
                .andExpect(status().isNoContent());

        // Then
        assertThat(repository.count()).isEqualTo(0);
    }

}