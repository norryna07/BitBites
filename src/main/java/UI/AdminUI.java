package main.java.UI;

import main.java.users.ConsoleService;

import java.util.Scanner;

public class AdminUI {
    private static AdminUI instance;
    private static final Scanner scanner = new Scanner(System.in);
    private AdminUI() {}

    public static AdminUI getInstance() {
        if (instance == null) {
            instance = new AdminUI();
        }
        return instance;
    }

    public static void showMenu() {
        System.out.println("Admin Menu:");
        System.out.println("1. Add new user");
        System.out.println("2. Get all users");
        System.out.println("3. Delete user");
        System.out.println("4. Get user by id");
        System.out.println("5. Get user by name");
        System.out.println("6. Update user");
        System.out.println("7. Exit");
        System.out.println("Enter your choice:");

        int choice = scanner.nextInt();
        scanner.nextLine();
    }
}
