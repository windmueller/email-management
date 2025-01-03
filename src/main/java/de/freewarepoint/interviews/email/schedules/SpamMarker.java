package de.freewarepoint.interviews.email.schedules;

import de.freewarepoint.interviews.email.EmailRepository;
import de.freewarepoint.interviews.email.entities.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Automatically marks all emails with a certain address as spam at 10:00 each day
 */
@Component
public class SpamMarker {

    private static final Logger log = LoggerFactory.getLogger(SpamMarker.class);

    private final EmailRepository emailRepository;

    @Autowired
    public SpamMarker(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void markSpamEmails() {
        final var mailsToMark = emailRepository.findByFrom("carl@gbtec.com").stream()
                .filter(m -> m.getState() != State.SPAM)
                .toList();

        log.info("Automatically marking {} emails as spam", mailsToMark.size());

        mailsToMark.forEach(m -> m.setState(State.SPAM));
        emailRepository.saveAll(mailsToMark);
    }

}
