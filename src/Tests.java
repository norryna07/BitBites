import main.java.exceptions.UserException;
import main.java.services.UsersService;
import main.java.users.PasswordUtils;
import main.java.users.User;
import main.java.users.UserRole;

public class Tests {
    public static void main(String[] args) {
        Exception e = new UserException("test");
        System.out.println(e.getClass().getName().split("\\.")[e.getClass().getName().split("\\.").length - 1]);
        System.out.println(UserRole.valueOf("admin".toUpperCase()));

        UsersService usersService = new UsersService();
        usersService.add(new User("admin11", PasswordUtils.hashPassword("admin"), UserRole.ADMIN));
        usersService.add(new User("cooker22", PasswordUtils.hashPassword("cooker"), UserRole.COOKER));
        usersService.add(new User("writer33", PasswordUtils.hashPassword("writer"), UserRole.WRITER));

        System.out.println(usersService.getAll());

        System.out.println(usersService.getById(1));

        System.out.println(usersService.getByName("admin"));

        User n = usersService.getById(9);

        usersService.update(new User(9, "news user", n.getHashedPassword(), n.getRole()));
    }
}
