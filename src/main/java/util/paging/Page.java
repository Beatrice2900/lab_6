package util.paging;

/**
 * A page of elements returned by a repository, together with the total number
 * of elements that exist, so the number of pages can be computed.
 *
 * @param <E> the type of the elements on the page
 */
public class Page<E> {
    private final Iterable<E> elementsOnPage;
    private final int totalNumberOfElements;

    /**
     * Creates a page.
     *
     * @param elementsOnPage        the elements on this page
     * @param totalNumberOfElements the number of elements in the whole repository, not only on this page
     */
    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements){
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }


    /**
     * Returns the elements on this page.
     *
     * @return the elements on this page
     */
    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    /**
     * Returns the total number of elements, counting all the pages.
     *
     * @return the total number of elements
     */
    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}
