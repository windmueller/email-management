package de.freewarepoint.interviews.email.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * An entity representing an "internet message" or email according to
 * <a href="https://datatracker.ietf.org/doc/html/rfc5322">RFC 5322</a>
 * with an additional state and update timestamp.
 */
@Entity
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @jakarta.validation.constraints.NotNull
    private State state = State.DRAFT;

    @Column(name = "fromHeader") // "from" is a reserved SQL keyword
    @NotBlank
    @jakarta.validation.constraints.Email
    private String from;

    @Column(name = "toHeader") // "to" is a reserved SQL keyword
    @ElementCollection
    private List<@jakarta.validation.constraints.Email String> to;

    @ElementCollection
    private List<@jakarta.validation.constraints.Email String> cc;

    @ElementCollection
    private List<@jakarta.validation.constraints.Email String> bcc;

    private String subject;

    private String body;

    @jakarta.validation.constraints.NotNull
    private ZonedDateTime date = ZonedDateTime.now();

    @jakarta.validation.constraints.NotNull
    @NotBlank
    private String charset = StandardCharsets.UTF_8.name();

    @jakarta.validation.constraints.NotNull
    private ZonedDateTime lastUpdate = ZonedDateTime.now();

    /**
     * @return the numeric id of the email message.
     */
    public long getId() {
        return id;
    }

    /**
     * @return the current {@link State} of this email, never {@code null}
     */
    public @NotNull State getState() {
        return state;
    }

    /**
     * Sets the {@link State} of this email
     *
     * @param state the new state, must not be {@code null}
     */
    public void setState(@NotNull State state) {
        this.state = state;
    }

    /**
     * @return the "From" header. May be empty but never {@code null}.
     */
    public @NotNull String getFrom() {
        return from;
    }

    /**
     * Sets the "From" header
     *
     * @param from the new header, must not be {@code null}
     */
    public void setFrom(@NotNull String from) {
        this.from = from;
    }

    /**
     * @return the list of email addresses to which the email should be sent in the order
     * they were initially specified. May be empty but never {@code null}.
     */
    public @NotNull List<String> getTo() {
        return to != null ? to : List.of();
    }

    /**
     * Specifies the list of email addresses to which the email should be sent
     *
     * @param to the list of recipients, must not be {@code null}
     */
    public void setTo(@NotNull List<String> to) {
        this.to = to;
    }

    /**
     * @return the list of email addresses to which the email should be sent as a copy in the order
     * they were initially specified. May be empty but never {@code null}.
     */
    public @NotNull List<String> getCc() {
        return cc != null ? cc : List.of();
    }

    /**
     * Specifies the list of email addresses to which the email should be sent as a copy
     *
     * @param cc the list of recipients, must not be {@code null}
     */
    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    /**
     * @return the list of email addresses to which the email should be sent as a blind copy in the order
     * they were initially specified. May be empty but never {@code null}.
     */
    public @NotNull List<String> getBcc() {
        return bcc != null ? bcc : List.of();
    }

    /**
     * Specifies the list of email addresses to which the email should be sent as a blind copy
     *
     * @param bcc the list of recipients, must not be {@code null}
     */
    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    /**
     * @return the subject of the email. May be empty but never {@code null}.
     */
    public @NotNull String getSubject() {
        return subject != null ? subject : "";
    }

    /**
     * Sets the subjects of the email.
     *
     * @param subject the new subject. Set to an empty string or {@code null} for deleting it.
     */
    public void setSubject(@Nullable String subject) {
        this.subject = subject;
    }

    /**
     * @return the subject of the email. May be empty but never {@code null}.
     */
    public @NotNull String getBody() {
        return body != null ? body : "";
    }

    /**
     * Sets the body of the email.
     *
     * @param body the new body. Set to an empty string or {@code null} for deleting it.
     */
    public void setBody(@Nullable String body) {
        this.body = body;
    }

    /**
     * Sets the date header of the email. This is the date "at which the creator
     * of the message indicated that the message was complete and ready to
     * enter the mail delivery system" (RFC 5322)
     *
     * @return the date, never {@code null}
     */
    public @NotNull ZonedDateTime getDate() {
        return date;
    }

    /**
     * Sets the date header of the email.
     *
     * @param date the new date, must not be {@code null}.
     */
    public void setDate(@NotNull ZonedDateTime date) {
        this.date = date;
    }

    /**
     * @return the name of the charset to use for the body, never {@code null} or empty.
     */
    public @NotNull String getCharset() {
        return charset;
    }

    /**
     * Sets the new charset.
     *
     * @param charset the new charset, must be supported by the JVM.
     */
    public void setCharset(@NotNull String charset) {
        try {
            Charset.forName(charset);
        } catch (final UnsupportedCharsetException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Charset " + charset + " is not supported");
        }

        this.charset = charset;
    }

    /**
     * Indicates the last time some change was made to this email.
     *
     * @return the latest update timestamp, never {@code null}.
     */
    public @NotNull ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the update timestamp to the specified value.
     *
     * @param lastUpdate the new update timestamp, must not be {@code null}.
     */
    public void setLastUpdate(@NotNull ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Ensures that the "last update" timestamp is updated
     */
    @PreUpdate
    public void preUpdate() {
        this.lastUpdate = ZonedDateTime.now();
    }

}
