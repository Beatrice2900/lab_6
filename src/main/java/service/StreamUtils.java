package service;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Helper methods for working with streams.
 */
public class StreamUtils {

    /**
     * Creates a sequential stream from an iterable.
     *
     * @param iterable the source of the elements
     * @param <T>      the type of the elements
     * @return a stream over the elements of the iterable
     */
    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}