package repository;

import domain.entities.Entity;
import domain.exceptions.DuplicateEntityException;
import domain.exceptions.EntityNotFoundException;
import domain.validators.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


/**
 * Repository that keeps the entities in memory, in a map, and validates them before saving.
 * If an id generator is provided, it is used to give an id to the entities saved without one.
 *
 * @param <ID> the type of the id of the entities
 * @param <E>  the type of the entities, which must extend {@link Entity}
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private Map<ID, E> entities = new HashMap<>();
    private Validator<E> validator;
    private Supplier<ID> idGenerator;

    /**
     * Creates an empty repository that does not generate ids.
     * The entities must already have an id when they are saved.
     *
     * @param validator the validator used to check the entities before saving or updating
     */
    public InMemoryRepository(Validator<E> validator) {
        this(validator, null);
    }

    /**
     * Creates an empty repository that generates the ids of the entities saved without one.
     *
     * @param validator   the validator used to check the entities before saving or updating
     * @param idGenerator supplies new ids; it may be null if the entities always have an id
     */
    public InMemoryRepository(Validator<E> validator, Supplier<ID> idGenerator) {
        this.validator = validator;
        this.idGenerator = idGenerator;
    }

    /**
     * Finds an entity by its id.
     *
     * @param id the id of the entity
     * @return the entity with the given id
     * @throws IllegalArgumentException                  if id is null
     * @throws domain.exceptions.EntityNotFoundException if no entity has this id
     */
    @Override
    public E findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null\n");
        }
        if (!entities.containsKey(id)) {
            throw new EntityNotFoundException("Entity with id " + id + " not found");
        }
        return entities.get(id);
    }

    /**
     * Returns all the entities from the repository.
     *
     * @return all the entities
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }


    /**
     * Validates and saves a new entity. If the entity has no id and the repository has an id
     * generator, the generated id is set on the entity. Generated ids that already exist are skipped.
     *
     * @param e the entity to save
     * @throws IllegalArgumentException                   if the entity is null
     * @throws domain.exceptions.ValidationException      if the entity is not valid
     * @throws domain.exceptions.DuplicateEntityException if the id already exists
     */
    @Override
    public void save(E e) {
        if (e == null) {
            throw new IllegalArgumentException("entity must not be null\n");
        }
        validator.validate(e);
        if (e.getId() == null && idGenerator != null) {
            ID id;
            do {
                id = idGenerator.get();
            } while (entities.containsKey(id));
            e.setId(id);
        }
        if (entities.containsKey(e.getId())) {
            throw new DuplicateEntityException("Entity with id " + e.getId() + " already exists\n");
        }
        entities.put(e.getId(), e);
    }

    /**
     * Validates the entity and replaces the stored entity that has the same id.
     *
     * @param e the entity with the new data
     * @return an empty Optional if the entity was updated, or the given entity if its id does not exist
     * @throws IllegalArgumentException              if the entity is null
     * @throws domain.exceptions.ValidationException if the entity is not valid
     */
    @Override
    public Optional<E> update(E e) {
        if (e == null) {
            throw new IllegalArgumentException("entity must not be null\n");
        }
        validator.validate(e);
        if (!entities.containsKey(e.getId())) {
            return Optional.of(e);
        }
        entities.put(e.getId(), e);
        return Optional.empty();
    }

    /**
     * Deletes the entity with the given id.
     *
     * @param id the id of the entity to delete
     * @return the deleted entity, or an empty Optional if no entity has this id
     * @throws IllegalArgumentException if id is null
     */
    @Override
    public Optional<E> delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null\n");
        }
        return Optional.ofNullable(entities.remove(id));
    }

}