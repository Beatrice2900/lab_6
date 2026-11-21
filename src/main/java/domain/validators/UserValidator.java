package domain.validators;
import domain.entities.User;
import domain.exceptions.ValidationException;


/**
 * Validates {@link User} objects: the id must be non-null and positive,
 * and the first and last name must be non-empty and start with an uppercase letter.
 */
public class UserValidator implements Validator<User>{

    /**
     * {@inheritDoc}
     * All the errors found are reported together in a single exception.
     *
     * @param entity the user to validate
     * @throws ValidationException if the id, first name or last name is invalid
     */
    @Override
    public void validate(User entity) {
        StringBuilder errors = new StringBuilder();
        if(entity.getId() != null && entity.getId() <=0 ){
            errors.append("Invalid id\n");
        }
        if(entity.getFirstName() == null || entity.getFirstName().isEmpty()){
            errors.append("First name can't be empty\n");
        }
        else if(!Character.isUpperCase(entity.getFirstName().charAt(0))){
            errors.append("First name must start with a uppercase letter\n");
        }
        if(entity.getLastName() == null || entity.getLastName().isEmpty()){
            errors.append("Last name can't be empty\n");
        }
        else if(!Character.isUpperCase(entity.getLastName().charAt(0))){
            errors.append("Last name must start with an uppercase letter\n");
        }

        if(errors.length() > 0){
            throw new ValidationException(errors.toString());
        }
    }
}
