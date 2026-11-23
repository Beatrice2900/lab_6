package repository;

import domain.entities.Friendship;
import domain.entities.Tuple;
import domain.entities.User;
import domain.exceptions.DuplicateEntityException;
import domain.exceptions.EntityNotFoundException;
import domain.validators.Validator;
import util.paging.Page;
import util.paging.Pageable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Repository that stores the friendships in a PostgreSQL database.
 * The id of a friendship is the pair (id1, id2), with id1 &lt; id2.
 */
@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship>,FriendsPagingRepository {
    private final Validator<Friendship> validator;
    private final String url;
    private final String user;
    private final String password;


    /**
     * Creates the repository with the validator and the connection parameters.
     *
     * @param validator the validator used to check the friendships before saving or updating
     * @param url       the url of the database
     * @param user      the database user
     * @param password  the database password
     */
    public FriendshipDBRepository(Validator<Friendship> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }


    /**
     * Finds a friendship by its id.
     *
     * @param id the id of the friendship, the pair of the two user ids
     * @return the friendship with the given id
     * @throws IllegalArgumentException                  if id is null
     * @throws domain.exceptions.EntityNotFoundException if no friendship has this id
     * @throws domain.exceptions.RepositoryException     if the database cannot be accessed
     */
    @Override
    public Friendship findOne(Tuple<Long, Long> id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null\n");
        }

        String sql = "SELECT friends_from FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new EntityNotFoundException("Entity with id " + id + " not found");
                }
                return new Friendship(id.getLeft(), id.getRight(),
                        resultSet.getTimestamp("friends_from").toLocalDateTime());
            }
        } catch (SQLException e) {
            throw SQLErrors.toRepositoryException("read friendship " + id, e);
        }
    }


    /**
     * Returns all the friendships from the database.
     *
     * @return all the friendships
     * @throws domain.exceptions.RepositoryException if the database cannot be accessed
     */
    @Override
    public Iterable<Friendship> findAll() {
        String sql = "SELECT id1, id2, friends_from FROM friendships";
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                friendships.add(new Friendship(resultSet.getLong("id1"),
                        resultSet.getLong("id2"),
                        resultSet.getTimestamp("friends_from").toLocalDateTime()));
            }
            return friendships;
        } catch (SQLException e) {
            throw SQLErrors.toRepositoryException("read friendships", e);
        }
    }


    /**
     * Validates and saves a new friendship.
     *
     * @param entity the friendship to save
     * @throws IllegalArgumentException                     if the friendship is null
     * @throws domain.exceptions.ValidationException        if the friendship is not valid
     * @throws domain.exceptions.DuplicateEntityException   if the friendship already exists
     * @throws domain.exceptions.RepositoryException        if the database cannot be accessed
     */
    @Override
    public void save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null\n");
        }
        validator.validate(entity);

        String sql = "INSERT INTO friendships (id1, id2, friends_from) VALUES (?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, entity.getUserId1());
            statement.setLong(2, entity.getUserId2());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            statement.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new DuplicateEntityException("Entity with id " + entity.getId() + " already exists\n");
            }
            throw SQLErrors.toRepositoryException("save friendship " + entity.getId(), e);
        }
    }


    /**
     * Validates the friendship and updates its date.
     *
     * @param entity the friendship with the new data
     * @return an empty Optional if the friendship was updated, or the given friendship if its id does not exist
     * @throws IllegalArgumentException                  if the friendship is null
     * @throws domain.exceptions.ValidationException     if the friendship is not valid
     * @throws domain.exceptions.RepositoryException     if the database cannot be accessed
     */
    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null\n");
        }
        validator.validate(entity);

        String sql = "UPDATE friendships SET friends_from = ? WHERE id1 = ? AND id2 = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            statement.setLong(2, entity.getUserId1());
            statement.setLong(3, entity.getUserId2());

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                return Optional.of(entity);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw SQLErrors.toRepositoryException("update friendship " + entity.getId(), e);
        }
    }


    /**
     * Deletes the friendship with the given id.
     *
     * @param id the id of the friendship to delete
     * @return the deleted friendship, or an empty Optional if no friendship has this id
     * @throws IllegalArgumentException                  if id is null
     * @throws domain.exceptions.RepositoryException     if the database cannot be accessed
     */
    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null\n");
        }

        String sql = "DELETE FROM friendships WHERE id1 = ? AND id2 = ? RETURNING friends_from";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Friendship(id.getLeft(), id.getRight(),
                        resultSet.getTimestamp("friends_from").toLocalDateTime()));
            }
        } catch (SQLException e) {
            throw SQLErrors.toRepositoryException("delete friendship " + id, e);
        }
    }


    /**
     * Returns the friends of a user, on the requested page, ordered by id, together with
     * the total number of friends of the user. The page is cut in SQL, not in Java, and both
     * queries use the same connection.
     *
     * @param userId   the id of the user whose friends are returned
     * @param pageable the page to return: its number (starting from 0) and its size
     * @return the friends on the page and the total number of friends
     * @throws IllegalArgumentException              if userId or pageable is null
     * @throws domain.exceptions.RepositoryException if the database cannot be accessed
     */
    @Override
    public Page<User> findFriendsOnPage(Long userId, Pageable pageable){
        if(userId == null) {
            throw new IllegalArgumentException("userId must not be null\n");
        }
        if(pageable == null) {
            throw new IllegalArgumentException("pageable must not be null\n");
        }

        String pageSql =
                "SELECT u.id, u.first_name, u.last_name " +
                        "FROM users u " +
                        "JOIN friendships f " +
                        "ON (u.id = f.id1 AND f.id2 = ?) OR (u.id = f.id2 AND f.id1 = ?) " +
                        "ORDER BY u.id " +
                        "LIMIT ? OFFSET ?";
        String countSql = "SELECT COUNT(*) FROM friendships WHERE id1=? OR id2=?";
        try(Connection connection = connect();
        PreparedStatement pageStatement = connection.prepareStatement(pageSql);
        PreparedStatement countStatement = connection.prepareStatement(countSql)) {

            pageStatement.setLong(1, userId);
            pageStatement.setLong(2,userId);
            pageStatement.setInt(3, pageable.getPageSize());
            pageStatement.setInt(4, pageable.getPageNumber() * pageable.getPageSize());

            List<User> friends = new ArrayList<>();
            try(ResultSet pageResult = pageStatement.executeQuery()){
                while(pageResult.next()){
                    friends.add(new User(pageResult.getLong("id"),
                            pageResult.getString("first_name"),
                            pageResult.getString("last_name")));
                }
            }

            countStatement.setLong(1,userId);
            countStatement.setLong(2,userId);

            int total=0;
            try(ResultSet countResult = countStatement.executeQuery()) {
                countResult.next();
                total = countResult.getInt(1);
            }
            return new Page<>(friends, total);
        }catch (SQLException e){
            throw SQLErrors.toRepositoryException("read friends page", e);
        }
    }
}