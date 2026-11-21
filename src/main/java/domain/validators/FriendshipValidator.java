package domain.validators;

import domain.entities.Friendship;
import domain.exceptions.ValidationException;

/**
 * Validates {@link Friendship} objects: the ids must be non-null and positive,
 * and the two users must be different.
 */
public class FriendshipValidator implements Validator<Friendship>{

    /**
     * {@inheritDoc}
     * All the errors found are reported together in a single exception.
     *
     * @param entity the friendship to validate
     * @throws ValidationException if any id is invalid or the two users are the same
     */
    @Override
    public void validate(Friendship entity){
        StringBuilder errors = new StringBuilder();
        Long UserId1 = entity.getUserId1();
        Long UserId2 = entity.getUserId2();
        if(UserId1 == null || UserId1 <=0 ){
            errors.append("Invalid first user Id\n");
        }
        if(UserId2 == null || UserId2 <=0 ){
            errors.append("Invalid second user Id\n");
        }
        if(UserId1 != null && UserId2 != null && UserId1.equals(UserId2)){
            errors.append("A user can't be friend with themselves\n");
        }
        if (entity.getDate() == null) {
            errors.append("Friendship date must not be null\n");
        }
        if(errors.length() > 0){
            throw new ValidationException(errors.toString());
        }
    }
}
