package domain.exceptions;
/**
 * Thrown when an entity with the requested id does not exist.
 */
public class EntityNotFoundException extends RuntimeException{

    /**
     * Creates an exception with a message.
     *
     * @param message the description of the problem
     */
    public EntityNotFoundException(String message){
        super(message);
    }
    /**
     * Creates an exception with a message and the original cause.
     *
     * @param message the description of the problem
     * @param cause   the exception that caused this one
     */
    public EntityNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
