package service;

import domain.entities.Friendship;
import domain.entities.Tuple;
import domain.entities.User;
import domain.exceptions.DuplicateEntityException;
import domain.exceptions.EntityNotFoundException;
import repository.Repository;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Service that manages the friendships between the users of the network.
 */
public class FriendshipService {
    private Repository<Long, User> userRepo;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepo;


    /**
     * Creates a friendship service.
     *
     * @param userRepo       the repository of users
     * @param friendshipRepo the repository of friendships
     */
    public FriendshipService(Repository<Long, User> userRepo, Repository<Tuple<Long, Long>, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * Adds a friendship between two existing users.
     * The order of the two ids does not matter.
     *
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     * @throws EntityNotFoundException                    if one of the users does not exist
     * @throws DuplicateEntityException                   if the friendship already exists
     * @throws domain.exceptions.ValidationException      if the ids are invalid or equal
     */
    public void addFriend(Long id1, Long id2){
        userRepo.findOne(id1);
        userRepo.findOne(id2);
        friendshipRepo.save(new Friendship(id1, id2, LocalDateTime.now()));
    }

    /**
     * Removes the friendship between two users.
     * The order of the two ids does not matter.
     *
     * @param id1 the id of the first user
     * @param id2 the id of the second user
     * @throws EntityNotFoundException if there is no friendship between the two users
     */
    public void removeFriend(Long id1, Long id2){
        if(id1 == null || id2 == null ){
            throw new IllegalArgumentException("ids must not be null");
        }
        friendshipRepo.delete(new Tuple<>(Math.min(id1, id2), Math.max(id1, id2)))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Friendship between " + id1 + " and " + id2 + " not found"));
    }

    /**
     * Returns all the friendships.
     *
     * @return all the friendships in the network
     */


    public List<Friendship> getAllFriendships() {
        return friendshipRepo.findAll(f -> true);
    }
}
