package ui;

import domain.entities.Friendship;
import domain.entities.User;
import domain.exceptions.DuplicateEntityException;
import domain.exceptions.EntityNotFoundException;
import domain.exceptions.RepositoryException;
import domain.exceptions.ValidationException;
import service.CriteriiUtilizator;
import service.FriendshipService;
import service.NetworkService;
import service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console user interface of the social network.
 */
public class ConsoleUi {
    private UserService userService;
    private FriendshipService friendshipService;
    private NetworkService networkService;
    private Scanner scanner = new Scanner(System.in);

    public ConsoleUi(UserService userService, FriendshipService friendshipService, NetworkService networkService){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.networkService = networkService;
    }

    private void printMenu(){
        System.out.println("\n1 - Add user");
        System.out.println("2 - Remove user");
        System.out.println("3 - Add friend");
        System.out.println("4 - Remove friend");
        System.out.println("5 - Number of communities");
        System.out.println("6 - Most sociable community");
        System.out.println("7 - Show users");
        System.out.println("8 - Show friendships");
        System.out.println("9 - Search users");
        System.out.println("10-Find user by id");
        System.out.println("0 - Exit");
        System.out.print("Option: ");
    }
    private Long readId(String label){
        System.out.print(label);
        return Long.parseLong(scanner.nextLine().trim());
    }
    private String readText(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    /**
     * Runs the main loop of the application until the user chooses to exit.
     * Errors are caught and displayed, so the program does not stop on invalid data.
     */
    public void run() {
        while(true){
            printMenu();
            if (!scanner.hasNextLine()) {
                break;
            }
            String option = scanner.nextLine().trim();
            if (option.equals("0")) {
                break;
            }
            try{
                switch (option) {
                    case "1": {
                        String firstName = readText("First name: ");
                        String lastName = readText("Last name: ");
                        Long id = userService.addUser(firstName, lastName);
                        System.out.println("User added with id " + id + ".");
                        break;
                    }
                    case "2": {
                        userService.removeUser(readId("Id: "));
                        System.out.println("User removed");
                        break;
                    }
                    case "3": {
                        Long id1 = readId("First user id: ");
                        Long id2 = readId("Second user id: ");
                        friendshipService.addFriend(id1, id2);
                        System.out.println("Friendship added.");
                        break;
                    }
                    case "4": {
                        Long id1 = readId("First user id: ");
                        Long id2 = readId("Second user id: ");
                        friendshipService.removeFriend(id1, id2);
                        System.out.println("Friendship removed.");
                        break;
                    }
                    case "5":
                        System.out.println("Communities: " + networkService.numberOfCommunities());
                        break;
                    case "6": {
                        System.out.println("Most sociable community:");
                        networkService.mostSociableCommunity().stream()
                                .map(userService::getUser)
                                .forEach(System.out::println);
                        break;
                    }
                    case "7":
                        userService.findUsers(u -> true).forEach(System.out::println);
                        break;
                    case "8":
                        friendshipService.getAllFriendships().forEach(System.out::println);
                        break;
                    case "9": {
                        String firstPrefix = readText("First name starts with: ");
                        String lastPrefix = readText("Last name starts with: ");
                        userService.findUsers(
                                        CriteriiUtilizator.prenumeIncepeCu(firstPrefix)
                                                .and(CriteriiUtilizator.numeIncepeCu(lastPrefix)))
                                .forEach(System.out::println);
                        break;
                    }
                    case "10": {
                        Long id = readId("Id: ");
                        Optional<User> user = userService.findUser(id);
                        user.ifPresent(System.out::println);
                        if (!user.isPresent()) {
                            System.out.println("User not found.");
                        }
                        break;
                    }
                    default:
                        System.out.println("Invalid option.");

                }
                } catch (NumberFormatException e){
                    System.out.println("Id must be a number.");
                }catch (ValidationException e){
                    System.out.println("Invalid data:\n" + e.getMessage());
                }catch (DuplicateEntityException e){
                    System.out.println("Already exists: " + e.getMessage());
                }catch(EntityNotFoundException e){
                    System.out.println("Not found: " + e.getMessage());
                }catch(RepositoryException e){
                    System.out.println("Persistence error: " + e.getMessage());
                }catch(IllegalArgumentException e){
                    System.out.println("Invalid argument: " + e.getMessage());
                }catch (UnsupportedOperationException e){
                    System.out.println("Operation not supported: " + e.getMessage());
            }
        }
    }
}
