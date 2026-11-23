package repository;

import domain.entities.Friendship;
import domain.entities.Tuple;
import domain.entities.User;
import domain.exceptions.RepositoryException;
import domain.validators.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Simple Factory that creates the repositories. It is the only place in the
 * application that knows the concrete repository classes. The type of persistence
 * ({@link RepositoryType}) and its parameters are read from {@code config.properties}.
 */
public class RepositoryFactory {
    private final Properties props = new Properties();
    private final Map<RepositoryType, Function<Validator<User>, Repository<Long,User >>> userCreators = new HashMap<>();
    private final Map<RepositoryType, BiFunction<Validator<Friendship>, Repository<Long,User>, Repository<Tuple<Long, Long>, Friendship>>> friendshipCreators = new HashMap<>();


    /**
     * Creates the factory and loads the configuration from {@code config.properties},
     * which must be in the classpath (src/main/resources).
     *
     * @throws RepositoryException if the file is missing or cannot be read
     */
    public RepositoryFactory() {
        try (InputStream in = RepositoryFactory.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new RepositoryException("config.properties not found");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RepositoryException("Could not read config.properties", e);
        }

        userCreators.put(RepositoryType.MEMORY,
                validator -> new InMemoryRepository<>(validator, new AtomicLong(0)::incrementAndGet));

        userCreators.put(RepositoryType.FILE,
                validator -> new FileRepository<>(validator, getRequired("repository.users.file"),
                        FileConverters::parseUser, FileConverters::userToLine,
                        new AtomicLong(0)::incrementAndGet));
        userCreators.put(RepositoryType.DATABASE,
                validator->new UserDBRepository(validator, getRequired("db.url"), getRequired("db.user"), getRequired("db.password")));

        friendshipCreators.put(RepositoryType.MEMORY,
                (validator, userRepo)-> new InMemoryRepository<>(validator));
        friendshipCreators.put(RepositoryType.FILE,
                (validator, userRepo) -> new FileRepository<>(
                        validator, getRequired("repository.friendships.file"),
                        line -> FileConverters.parseFriendship(line, userRepo),
                        FileConverters::friendshipToLine,
                        null
                ));
        friendshipCreators.put(RepositoryType.DATABASE,
                (validator, userRepo)-> new FriendshipDBRepository(
                        validator, getRequired("db.url"), getRequired("db.user"), getRequired("db.password")
                ));
    }


    private String getRequired(String key) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RepositoryException("Missing configuration: " + key);
        }
        return value.trim();
    }

    private RepositoryType getType() {
        String type = getRequired("repository.type");
        try {
            return RepositoryType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new RepositoryException("Unknown repository type: " + type, e);
        }
    }


    /**
     * Creates the repository of users, of the type given in the configuration.
     * For {@code FILE} the users are loaded from the configured file.
     *
     * @param validator the validator used to check the users
     * @return the repository of users
     * @throws RepositoryException if a configuration value is missing or unknown,
     *                             or if the data cannot be read
     */
    public Repository<Long, User> createUserRepository(Validator<User> validator) {
        RepositoryType type = getType();
        Function<Validator<User>, Repository<Long, User>> creator = userCreators.get(type);
        if(creator == null ){
            throw new RepositoryException("Unsupported repository type: " + type);
        }
        return creator.apply(validator);
    }

    /**
     * Creates the repository of friendships, of the same type as the user repository.
     * For {@code FILE}, only friendships between existing users are loaded, so the
     * user repository must be created first.
     *
     * @param validator the validator used to check the friendships
     * @param userRepo  the repository of users, used to check that the users exist
     * @return the repository of friendships
     * @throws RepositoryException if a configuration value is missing or unknown,
     *                             or if the data cannot be read
     */
    public Repository<Tuple<Long, Long>, Friendship> createFriendshipRepository(
            Validator<Friendship> validator, Repository<Long, User> userRepo) {
        RepositoryType type = getType();
        BiFunction<Validator<Friendship>, Repository<Long, User>,
                Repository<Tuple<Long, Long>, Friendship>> creator = friendshipCreators.get(type);
        if(creator == null){
            throw new RepositoryException("Unsupported repository type: " + type);
        }
        return creator.apply(validator, userRepo);
    }

    /**
     * Reads the page size used by the user interface from the configuration.
     *
     * @return the number of elements shown on one page
     * @throws RepositoryException if the property is missing, is not a number or is not positive
     */
    public int getPageSize() {
        String value = getRequired("ui.page.size");
        int pageSize;
        try {
            pageSize = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RepositoryException("ui.page.size must be a number");
        }
        if (pageSize <= 0) {
            throw new RepositoryException("ui.page.size must be greater than 0");
        }
        return pageSize;
    }
}
