package de.freewarepoint.interviews.email.validators;

import de.freewarepoint.interviews.email.entities.Email;
import de.freewarepoint.interviews.email.entities.State;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Ensures that only emails with the {@link State#DRAFT} may be modified
 */
public class ChangeAllowedValidator implements Validator {

    @Override
    public boolean supports(@NotNull Class<?> clazz) {
        return clazz == Email.class;
    }

    @Override
    public void validate(@NotNull Object target, @NotNull Errors errors) {
        if (!(target instanceof final Email email)) {
            throw new IllegalStateException("Object must be of type Email, should have been checked by 'supports' method");
        }

        if (email.getState() != State.DRAFT) {
            errors.reject("Updating is only allowed for draft messages");
        }
    }
}
