package repository;

import domain.entities.User;
import util.paging.Page;
import util.paging.Pageable;

public interface FriendsPagingRepository {

    /**
     * Returns the friends of a user on the requested page, ordered by id, together with
     * the total number of friends of the user.
     *
     * @param userId   the id of the user whose friends are returned
     * @param pageable the page to return: its number (starting from 0) and its size
     * @return the friends on the page and the total number of friends
     * @throws IllegalArgumentException              if userId or pageable is null
     * @throws domain.exceptions.RepositoryException if the data cannot be read
     */
    Page<User> findFriendsOnPage(Long userId, Pageable pageable);
}
