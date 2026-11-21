package domain.entities;

import java.util.Objects;

/**
 * Base class for all entities that are identified by an id.
 *
 * @param <ID> the type of the id
 */
public class Entity<ID> {
    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Compares this entity with another object. They are equal if the other object
     * is not {@code null}, has exactly the same class and has an equal id.
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o){
        if(this == o ) return true;
        if(o==null || getClass() != o.getClass()) return false;
        Entity<?> other = (Entity<?>) o;
        return Objects.equals(id, other.id);
    }

    /**
     * Returns a hash code computed from the id, consistent with {@link #equals(Object)}.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
