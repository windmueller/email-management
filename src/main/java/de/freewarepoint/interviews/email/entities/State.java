package de.freewarepoint.interviews.email.entities;

/**
 * Each {@link Email} has one of these defined states.
 */
public enum State {

    /**
     * An email that has not yet been sent and can still be edited
     */
    DRAFT,

    /**
     * An email that has been sent
     */
    SENT,

    /**
     * An incoming or outgoing email that has been deleted
     */
    DELETED,

    /**
     * Incoming emails marked with this state are considered as unsolicited messages
     */
    SPAM

}
