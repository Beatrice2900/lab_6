package domain.exceptions;
/**
 * Thrown when trying to save an entity whose id already exists.
 */
public class DuplicateEntityException extends RuntimeException{
    /**
     * Creates an exception with a message.
     *
     * @param message the description of the problem
     */
    public DuplicateEntityException(String message){
        super(message);
    }

    /**
     * Creates an exception with a message and the original cause.
     *
     * @param message the description of the problem
     * @param cause   the exception that caused this one
     */
    public DuplicateEntityException(String message, Throwable cause){
        super(message, cause);
    }
}
