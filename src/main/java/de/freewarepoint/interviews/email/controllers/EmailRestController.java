package de.freewarepoint.interviews.email.controllers;

import de.freewarepoint.interviews.email.EmailRepository;
import de.freewarepoint.interviews.email.entities.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Provides batch operations not supported by Spring Data REST, see
 * <a href="https://github.com/spring-projects/spring-data-rest/issues/717">Spring Data #717</a>
 */
@RepositoryRestController
public class EmailRestController {

    private final EmailRepository emailRepository;

    @Autowired
    public EmailRestController(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    /**
     * Inserts multiple {@link Email} entities at once
     *
     * @param emails the emails to insert
     *
     * @return Response with HTTP status 200
     */
    @PostMapping(value = "/emails/batch-insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> batchInsert(@RequestBody List<Email> emails) {
        emailRepository.saveAll(emails);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes multiple {@link Email} entities at once
     *
     * @param emailIds the ids of the emails to delete
     *
     * @return Response with HTTP status 204
     */
    @PostMapping(value = "/emails/batch-delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> batchDelete(@RequestBody List<Long> emailIds) {
        emailIds.forEach(emailRepository::deleteById);
        return ResponseEntity.noContent().build();
    }

}
