package de.freewarepoint.interviews.email.validators;

import de.freewarepoint.interviews.email.EmailRepository;
import de.freewarepoint.interviews.email.entities.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.TransactionException;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class EmailAddressValidatorsTest {

    @Autowired
    private EmailRepository repository;

    @BeforeEach
    void clearRepository() {
        repository.deleteAll();
    }

    @Test
    void validFrom() {
        performValidTest(email -> email.setFrom("gkar@narn.gov"));
    }

    @Test
    void invalidFrom() {
        performInvalidTest(email -> email.setFrom("gkarnarn.gov"));
    }

    @Test
    void emptyTo() {
        performValidTest(email -> email.setTo(List.of()));
    }

    @Test
    void validTo() {
        performValidTest(email -> email.setTo(List.of("natoth@narn.gov")));
    }

    @Test
    void invalidTo() {
        performInvalidTest(email -> email.setTo(List.of("natothnarn.gov")));
    }

    @Test
    void emptyCc() {
        performValidTest(email -> email.setCc(null));
    }

    @Test
    void validCc() {
        performValidTest(email -> email.setCc(List.of("natoth@narn.gov")));
    }

    @Test
    void invalidCc() {
        performInvalidTest(email -> email.setCc(List.of("natothnarn.gov")));
    }

    @Test
    void emptyBcc() {
        performValidTest(email -> email.setBcc(null));
    }

    @Test
    void validBcc() {
        performValidTest(email -> email.setBcc(List.of("natoth@narn.gov")));
    }

    @Test
    void invalidBcc() {
        performInvalidTest(email -> email.setBcc(List.of("natothnarn.gov")));
    }

    private void performValidTest(final Consumer<Email> action) {
        // Given
        assertThat(repository.count()).isZero();
        final var email = new Email();
        email.setFrom("gkar@narn.gov");
        action.accept(email);

        // When
        repository.save(email);

        // Then
        assertThat(repository.count()).isOne();
    }

    private void performInvalidTest(final Consumer<Email> action) {
        // Given
        assertThat(repository.count()).isZero();
        final var email = new Email();
        email.setFrom("gkar@narn.gov");
        action.accept(email);

        // When
        assertThatExceptionOfType(TransactionException.class).isThrownBy(() -> repository.save(email));

        // Then
        assertThat(repository.count()).isZero();
    }

}
