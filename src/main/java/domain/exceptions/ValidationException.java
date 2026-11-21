package domain.exceptions;
/**
 * Thrown when an entity does not respect the validation rules.
 * The message contains all the errors that were found.
 */
public class ValidationException extends RuntimeException{

    /**
     * Creates a validation exception with a message.
     *
     * @param message the description of the validation errors
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Creates a validation exception with a message and the original cause.
     *
     * @param message the description of the validation errors
     * @param cause   the exception that caused this one
     */
    public ValidationException(String message, Throwable cause){
        super(message, cause);
    }
}
