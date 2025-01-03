package de.freewarepoint.interviews.email.schedules;

import de.freewarepoint.interviews.email.EmailRepository;
import de.freewarepoint.interviews.email.entities.Email;
import de.freewarepoint.interviews.email.entities.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpamMarkerTest {

    @Autowired
    private EmailRepository repository;

    @Autowired
    private SpamMarker spamMarker;

    @BeforeEach
    void clearRepository() {
        repository.deleteAll();
    }

    @Test
    void automaticallyMarkAsSpam() {
        // Given
        var email = new Email();
        email.setFrom("carl@gbtec.com");
        email.setState(State.SENT);
        email = repository.save(email);
        final var timestampAfterCreate = email.getLastUpdate();

        // When
        spamMarker.markSpamEmails();

        // Then
        final var updatedEmail = repository.findById(email.getId());
        assertThat(updatedEmail).isPresent();

        email = updatedEmail.get();
        assertThat(email.getState()).isEqualTo(State.SPAM);
        assertThat(email.getLastUpdate()).isAfter(timestampAfterCreate);
    }

    @Test
    void doNotMarkOtherEmails() {
        // Given
        var email = new Email();
        email.setFrom("delenn@minbari.gov");
        email.setState(State.SENT);
        email = repository.save(email);

        // When
        spamMarker.markSpamEmails();

        // Then
        final var updatedEmail = repository.findById(email.getId());
        assertThat(updatedEmail).isPresent();

        email = updatedEmail.get();
        assertThat(email.getState()).isEqualTo(State.SENT);
    }

}