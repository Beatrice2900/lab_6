package domain.entities;


/**
 * Represents a user of the social network.
 * A user is identified by a {@code Long} id and has a first name and a last name.
 */
public class User extends Entity<Long>{
    private String lastName;
    private String firstName;

    /**
     * Creates a user.
     *
     * @param id        the id of the user
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     */
    public User(Long id, String firstName, String lastName){
        setId(id);
        this.lastName = lastName;
        this.firstName = firstName;
    }
    /**
     * Returns the last name of the user.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the first name of the user.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns a textual representation of the user, in the form "id firstName lastName".
     *
     * @return the user as a string
     */
    @Override
    public String toString() {
        return getId() + " " + firstName + " " + lastName;
    }
}
