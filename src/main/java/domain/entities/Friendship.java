package domain.entities;


import java.time.LocalDateTime;

/**
 * Represents a friendship between two users of the social network.
 * The friendship has its own {@code Long} id and stores the ids of the two users.
 */
public class Friendship extends Entity<Tuple<Long, Long>>{
    private LocalDateTime date;

    /**
     * Creates a friendship between two users.
     *
     * @param UserId1 the id of the first user
     * @param UserId2 the id of the second user
     */
    public Friendship(Long UserId1, Long UserId2, LocalDateTime date){
        if(UserId1 != null && UserId2 != null){
            setId(new Tuple<>(Math.min(UserId1, UserId2), Math.max(UserId1,UserId2)));
        }else {
            setId(new Tuple<>(UserId1, UserId2));
        }
        this.date = date;
    }
    /**
     * Returns the id of the first user.
     *
     * @return the id of the first user
     */
    public Long getUserId1(){
        return getId().getLeft();
    }
    /**
     * Returns the id of the second user.
     *
     * @return the id of the second user
     */
    public Long getUserId2(){
        return getId().getRight();
    }

    /**
     * Returns a textual representation of the friendship, in the form "id: userId1 - userId2".
     *
     * @return the friendship as a string
     */

    public LocalDateTime getDate(){
        return date;
    }

    @Override
    public String toString() {
        return getUserId1() + " - " + getUserId2() + " (" + date + ")";
    }



}
