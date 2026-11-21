package repository;

import domain.entities.Entity;
import domain.exceptions.DuplicateEntityException;
import domain.exceptions.RepositoryException;
import domain.exceptions.ValidationException;
import domain.validators.Validator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Repository that keeps the entities in memory, like {@link InMemoryRepository}, and also
 * stores them in a text file, one entity per line. The file is read when the repository is
 * created and rewritten after every change.
 * The conversion between an entity and a line is given by two functions, so the same class
 * works for any type of entity.
 *
 * @param <ID> the type of the id of the entities
 * @param <E>  the type of the entities, which must extend {@link Entity}
 */
public class FileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final Path path;
    private final Function<String, E> lineToEntity;
    private final Function<E, String> entityToLine;

    /**
     * Creates the repository and loads the entities from the file, if it exists.
     * The lines that cannot be loaded (invalid or duplicated) are ignored and reported on the console.
     *
     * @param validator    the validator used to check the entities
     * @param fileName     the path of the file where the entities are stored
     * @param lineToEntity converts a line of the file into an entity
     * @param entityToLine converts an entity into a line of the file
     * @param idGenerator  supplies new ids for the entities saved without one; it may be null
     * @throws domain.exceptions.RepositoryException if the file cannot be read
     */
    public FileRepository(Validator<E> validator, String fileName, Function<String, E> lineToEntity,
                          Function<E, String> entityToLine, Supplier<ID> idGenerator) {
        super(validator, idGenerator);
        this.path = Paths.get(fileName);
        this.lineToEntity = lineToEntity;
        this.entityToLine = entityToLine;
        loadFromFile();
    }

    /**
     * Reads the file and adds its entities to the repository, without writing the file again.
     * Empty lines are skipped, and the lines that cannot be loaded are ignored with a message.
     *
     * @throws domain.exceptions.RepositoryException if the file cannot be read
     */
    private void loadFromFile() {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            List<String> lines = stream.collect(Collectors.toList());
            IntStream.range(0, lines.size())
                    .filter(i -> !lines.get(i).trim().isEmpty())
                    .forEach(i -> {
                        try {
                            saveWithoutWriting(lineToEntity.apply(lines.get(i)));
                        } catch (ValidationException | DuplicateEntityException | RepositoryException e) {
                            System.out.println("Ignored line " + (i + 1) + " of " + path + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            throw new RepositoryException("Could not read file " + path, e);
        }
    }

    /**
     * Saves the entity in memory only, without writing the file.
     * It is used while the file is loaded.
     *
     * @param e the entity to save
     */
    private void saveWithoutWriting(E e) {
        super.save(e);
    }

    /**
     * Writes all the entities to the file, replacing its content.
     * The parent directory is created if it does not exist.
     *
     * @throws domain.exceptions.RepositoryException if the file cannot be written
     */
    private void writeToFile() {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            List<String> lines = StreamSupport.stream(findAll().spliterator(), false)
                    .map(entityToLine)
                    .collect(Collectors.toList());
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RepositoryException("Could not write file " + path, e);
        }
    }

    /**
     * Updates the entity and, if it was updated, rewrites the file.
     *
     * @param e the entity with the new data
     * @return an empty Optional if the entity was updated, or the given entity if its id does not exist
     * @throws IllegalArgumentException              if the entity is null
     * @throws domain.exceptions.ValidationException if the entity is not valid
     * @throws domain.exceptions.RepositoryException if the file cannot be written
     */
    @Override
    public Optional<E> update(E e) {
        Optional<E> result = super.update(e);
        if (!result.isPresent()) {
            writeToFile();
        }
        return result;
    }

    /**
     * Validates and saves a new entity, then rewrites the file.
     *
     * @param e the entity to save
     * @throws IllegalArgumentException                   if the entity is null
     * @throws domain.exceptions.ValidationException      if the entity is not valid
     * @throws domain.exceptions.DuplicateEntityException if the id already exists
     * @throws domain.exceptions.RepositoryException      if the file cannot be written
     */
    @Override
    public void save(E e) {
        super.save(e);
        writeToFile();
    }

    /**
     * Deletes the entity with the given id and, if it existed, rewrites the file.
     *
     * @param id the id of the entity to delete
     * @return the deleted entity, or an empty Optional if no entity has this id
     * @throws IllegalArgumentException              if id is null
     * @throws domain.exceptions.RepositoryException if the file cannot be written
     */
    @Override
    public Optional<E> delete(ID id) {
        Optional<E> result = super.delete(id);
        if (result.isPresent()) {
            writeToFile();
        }
        return result;
    }

}