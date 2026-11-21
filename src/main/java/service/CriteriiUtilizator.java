package service;

import domain.entities.User;

import java.util.function.Predicate;

/**
 * Conditions used to search users, as predicates that can be passed to the service.
 */
public class CriteriiUtilizator {

    /**
     * Creates a condition that is true for the users whose first name starts with a prefix.
     *
     * @param prefix the prefix the first name must start with
     * @return a predicate that is true for the matching users
     */
    public static Predicate<User> prenumeIncepeCu(String prefix) {
        return user -> user.getFirstName() != null && user.getFirstName().startsWith(prefix);
    }

    /**
     * Creates a condition that is true for the users whose last name starts with a prefix.
     *
     * @param prefix the prefix the last name must start with
     * @return a predicate that is true for the matching users
     */
    public static Predicate<User> numeIncepeCu(String prefix) {
        return user -> user.getLastName() != null && user.getLastName().startsWith(prefix);
    }
}