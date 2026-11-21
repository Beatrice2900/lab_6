package domain.validators;

/**
 * Contract for validating objects of a given type.
 * Each implementation defines its own validation rules (Strategy Pattern).
 *
 * @param <T> the type of the objects to be validated
 */


@FunctionalInterface
public interface Validator<T> {
    /**
     * Validates the given entity.
     * Does nothing if the entity is valid.
     *
     * @param entity the entity to validate
     * @throws domain.exceptions.ValidationException if the entity is not valid
     */
    void validate(T entity); //singura metoda abstracta care ramane

    default Validator<T> andThen(Validator<T> other) {
        return entity -> {
          this.validate(entity);
          other.validate(entity);
        };
    }
}
