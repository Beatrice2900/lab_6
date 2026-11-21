package repository;

import domain.entities.Entity;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * Contract for a repository that stores entities identified by an id.
 *
 * @param <ID> the type of the id of the entities
 * @param <E>  the type of the entities, which must extend {@link Entity}
 */
public interface Repository <ID, E extends Entity<ID>>{

     E findOne(ID id);
     Iterable<E> findAll();
     void save(E e);
     Optional<E> update(E e);
     Optional<E> delete(ID id);

     default List<E> findAll(Predicate<E> filter){
          return StreamSupport.stream(findAll().spliterator(), false)
                  .filter(filter)
                  .collect(Collectors.toList());
     }

}
