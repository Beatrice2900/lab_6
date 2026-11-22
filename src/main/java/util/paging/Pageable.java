package util.paging;

/**
 * Describes the page requested from a repository: its number and its size.
 * Pages are numbered starting from 0.
 */
public class Pageable {
    private final int pageNumber;
    private final int pageSize;

    /**
     * Creates a page request.
     *
     * @param pageNumber the number of the requested page, starting from 0
     * @param pageSize   the maximum number of elements on a page
     */
    public Pageable(int pageNumber, int pageSize){
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * Returns the number of the requested page.
     *
     * @return the page number, starting from 0
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Returns the maximum number of elements on a page.
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }
}
