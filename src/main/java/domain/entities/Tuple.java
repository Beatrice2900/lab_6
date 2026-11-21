package domain.entities;

import java.util.Objects;


/**
 * An immutable pair of two values. It is used, for example, as the id of a
 * friendship (the pair of user ids), so it can be a key in a map.
 *
 * @param <E1> the type of the first value
 * @param <E2> the type of the second value
 */
public class Tuple<E1,E2> {
    private final E1 left;
    private final E2 right;

    public Tuple(E1 left, E2 right) {
        this.left = left;
        this.right = right;
    }

    public E1 getLeft() {
        return left;
    }

    public E2 getRight() {
        return right;
    }

    /**
     * Compares this pair with another object. They are equal if the other object
     * is a {@code Tuple} (and not {@code null}) with equal first and second values.
     *
     * @param o the object to compare with
     * @return {@code true} if the objects are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Tuple<?,?> other = (Tuple<?,?>) o;
        return Objects.equals(left, other.left) && Objects.equals(right, other.right);
    }


    /**
     * Returns a hash code computed from both values, consistent with {@link #equals(Object)}.
     *
     * @return the hash code
     */
    @Override
    public int hashCode(){
        return Objects.hash(left, right);
    }


    /**
     * Returns the pair as text, in the form "(left, right)".
     *
     * @return the textual representation of the pair
     */
    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}
