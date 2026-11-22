package repository;

import domain.entities.Entity;
import util.paging.Page;
import util.paging.Pageable;

/**
 * Repository that can also return its entities page by page.
 *
 * @param <ID> the type of the id of the entities
 * @param <E>  the type of the entities, which must extend {@link Entity}
 */
public interface PagingRepository<ID,E extends Entity<ID>> extends Repository<ID,E>{

    /**
     * Returns the entities on the requested page.
     *
     * @param pageable the page to return: its number (starting from 0) and its size
     * @return the elements on the page and the total number of elements
     * @throws domain.exceptions.RepositoryException if the data cannot be read
     */
    Page<E> findAllOnPage(Pageable pageable);
}
