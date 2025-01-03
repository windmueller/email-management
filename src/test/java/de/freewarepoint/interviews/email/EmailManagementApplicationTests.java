package de.freewarepoint.interviews.email;

import de.freewarepoint.interviews.email.entities.Email;
import de.freewarepoint.interviews.email.entities.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class EmailManagementApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private EmailRepository repository;

	@BeforeEach
	void clearRepository() {
		repository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void createEmailDraft() throws Exception {
		// Given
		final var address = "john.sheridan@earth-alliance.org";

		assertThat(repository.count()).isEqualTo(0);
		assertThat(repository.findByFrom(address)).isEmpty();

		// When
		mvc.perform(post("/emails")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                                { "from" : "%s" }
                                """.formatted(address)))
				.andExpect(status().isCreated());

		// Then
		final List<Email> emails = repository.findByFrom(address);
		assertThat(emails).hasSize(1);
		assertThat(emails.getFirst().getState()).isSameAs(State.DRAFT);
	}

	@Test
	void createCompleteSentMail() throws Exception {
		// Given
		assertThat(repository.count()).isZero();

		// When
		mvc.perform(post("/emails")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                                {
                                	"from": "alfred.bester@psi-corps.org",
                                    "to": ["talia.winters@earth-alliance.org"],
                                    "cc": ["superiors@psi-corps.org"],
                                    "bcc": ["alfred.bester@psi-corps.org"],
                                    "subject": "Activate",
                                    "body": "It's time",
                                    "charset": "UTF-8",
                                    "state": "SENT"
                                }
                                """))
				.andExpect(status().isCreated());

		// Then
		assertThat(repository.count()).isOne();
	}

	@Test
	@WithAnonymousUser
	void fetchEmailAnonymous() throws Exception {
		mvc.perform(get("/emails"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void fetchEmail() throws Exception {
		// Given
		var email = new Email();
		email.setFrom("lennier@minbari.gov");
		email = repository.save(email);

		// When
		mvc.perform(get("/emails/" + email.getId()))
				// Then
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/hal+json"))
				.andExpect(jsonPath("$.state").value("DRAFT"))
				.andExpect(jsonPath("$.from").value("lennier@minbari.gov"))
				.andExpect(jsonPath("$.to").isArray())
				.andExpect(jsonPath("$.cc").isArray())
				.andExpect(jsonPath("$.bcc").isArray())
				.andExpect(jsonPath("$.to", hasSize(0)))
				.andExpect(jsonPath("$.cc", hasSize(0)))
				.andExpect(jsonPath("$.bcc", hasSize(0)));
	}

	@Test
	void updateEmail() throws Exception {
		// Given
		final var address = "susan.ivanova@earth-alliance.org";
		var email = new Email();
		email.setFrom(address);
		email = repository.save(email);

		assertThat(repository.findByFrom(address)).hasSize(1);

		// When
		mvc.perform(patch("/emails/" + email.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                                { "subject" : "Important update" }
                                """))
				.andExpect(status().isNoContent());

		// Then
		assertThat(repository.findByFrom(address)).hasSize(1);
		final var updatedEmail = repository.findById(email.getId());
		assertThat(updatedEmail).isPresent();
		assertThat(updatedEmail.get().getSubject()).isEqualTo("Important update");
	}

	@Test
	void timestampAutomaticallyUpdated() throws Exception {
		// Given
		final var address = "londo.mollari@centauri.gov";
		var email = new Email();
		email.setFrom(address);
		email = repository.save(email);

		final var timestampAfterCreation = email.getLastUpdate();

		// When
		mvc.perform(patch("/emails/" + email.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                                { "subject" : "Important update" }
                                """))
				.andExpect(status().isNoContent());

		// Then
		final var updatedEmail = repository.findById(email.getId());
		assertThat(updatedEmail).isPresent();
		assertThat(updatedEmail.get().getLastUpdate()).isAfter(timestampAfterCreation);
	}

}
