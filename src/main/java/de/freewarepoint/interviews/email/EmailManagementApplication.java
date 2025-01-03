package de.freewarepoint.interviews.email;

import de.freewarepoint.interviews.email.validators.ChangeAllowedValidator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmailManagementApplication implements RepositoryRestConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(EmailManagementApplication.class, args);
	}

	@Override
	public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
		validatingListener.addValidator("beforeSave", new ChangeAllowedValidator());
	}

}
