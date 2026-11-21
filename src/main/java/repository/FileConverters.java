package repository;

import domain.entities.Friendship;
import domain.entities.User;
import domain.exceptions.EntityNotFoundException;
import domain.exceptions.RepositoryException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Methods that convert users and friendships to and from the lines of a text file.
 * The fields of a line are separated by semicolons.
 */
public class FileConverters {

    /**
     * Converts a line of the form {@code id;firstName;lastName} into a user.
     *
     * @param line the line read from the file
     * @return the user described by the line
     * @throws domain.exceptions.RepositoryException if the line does not have 3 fields or the id is not a number
     */
    public static User parseUser(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length != 3) {
            throw new RepositoryException("Expected 3 fields but found " + parts.length);
        }
        try {
            Long id = Long.parseLong(parts[0].trim());
            return new User(id, parts[1].trim(), parts[2].trim());
        } catch (NumberFormatException e) {
            throw new RepositoryException("Invalid id: " + parts[0], e);
        }
    }

    /**
     * Converts a user into a line of the form {@code id;firstName;lastName}.
     *
     * @param user the user to convert
     * @return the line to write in the file
     */
    public static String userToLine(User user) {
        return user.getId() + ";" + user.getFirstName() + ";" + user.getLastName();
    }

    /**
     * Converts a line of the form {@code id1;id2;date} into a friendship.
     * Both users must exist in the given repository.
     *
     * @param line     the line read from the file
     * @param userRepo the repository used to check that the two users exist
     * @return the friendship described by the line
     * @throws domain.exceptions.RepositoryException if the line does not have 3 fields, if an id is not a number,
     *                                               if the date is not valid, or if a user does not exist
     */
    public static Friendship parseFriendship(String line, Repository<Long, User> userRepo) {
        String[] parts = line.split(";", -1);
        if (parts.length != 3) {
            throw new RepositoryException("Expected 3 fields but found " + parts.length);
        }
        Long id1;
        Long id2;
        LocalDateTime date;
        try {
            id1 = Long.parseLong(parts[0].trim());
            id2 = Long.parseLong(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new RepositoryException("Invalid user id in line: " + line, e);
        }
        try {
            date = LocalDateTime.parse(parts[2].trim());
        } catch (DateTimeParseException e) {
            throw new RepositoryException("Invalid date: " + parts[2], e);
        }
        try {
            userRepo.findOne(id1);
            userRepo.findOne(id2);
        } catch (EntityNotFoundException e) {
            throw new RepositoryException("Friendship refers to a user that does not exist: " + line, e);
        }
        return new Friendship(id1, id2, date);
    }

    /**
     * Converts a friendship into a line of the form {@code id1;id2;date}.
     *
     * @param f the friendship to convert
     * @return the line to write in the file
     */
    public static String friendshipToLine(Friendship f) {
        return f.getUserId1() + ";" + f.getUserId2() + ";" + f.getDate();
    }
}