package de.freewarepoint.interviews.email.validators;

import de.freewarepoint.interviews.email.entities.Email;
import de.freewarepoint.interviews.email.EmailRepository;
import de.freewarepoint.interviews.email.entities.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class ChangeAllowedValidatorTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmailRepository repository;

    @BeforeEach
    void clearRepository() {
        repository.deleteAll();
    }

    @Test
    void changeNotAllowedForSentMails() throws Exception {
        // Given
        var email = new Email();
        email.setFrom("susan.ivanova@earth-alliance.org");
        email.setTo(List.of("michael.garibaldi@earth-alliance.org"));
        email.setSubject("Security detail");
        email.setState(State.SENT);
        email = repository.save(email);

        // When
        mvc.perform(patch("/emails/" + email.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "subject" : "Unimportant stuff" }
                                """))
                .andExpect(status().isBadRequest()); // Then
    }

}