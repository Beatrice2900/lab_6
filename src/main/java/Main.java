import domain.entities.Friendship;
import domain.entities.Tuple;
import domain.entities.User;
import domain.exceptions.RepositoryException;
import domain.exceptions.ValidationException;
import domain.validators.FriendshipValidator;
import domain.validators.UserValidator;
import domain.validators.Validator;
import repository.Repository;
import repository.RepositoryFactory;
import service.FriendshipService;
import service.NetworkService;
import service.UserService;
import ui.ConsoleUi;

public class Main {
    public static void main(String[] args) {
        try {
            RepositoryFactory factory = new RepositoryFactory();
            Validator<User> minLength = user ->{
              if(user.getFirstName() != null && user.getFirstName().length() < 2) {
                  throw new ValidationException("First name is too short");
              }
              if(user.getLastName() != null && user.getLastName().length() < 2){
                  throw new ValidationException("Last name is too short");
              }
            };

            Validator<User> userValidator = new UserValidator().andThen(minLength);
            Repository<Long, User> userRepo = factory.createUserRepository(userValidator);

            Repository<Tuple<Long, Long>, Friendship> friendshipRepo =
                    factory.createFriendshipRepository(new FriendshipValidator(), userRepo);

            UserService userService = new UserService(userRepo, friendshipRepo);
            FriendshipService friendshipService = new FriendshipService(userRepo, friendshipRepo);
            NetworkService networkService = new NetworkService(userRepo, friendshipRepo);

            new ConsoleUi(userService, friendshipService, networkService).run();
        } catch (RepositoryException e) {
            System.out.println("Could not start the application: " + e.getMessage());
        }
    }
}