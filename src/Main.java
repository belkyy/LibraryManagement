import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static LibraryManager manager = new LibraryManager();
    static Random random = new Random();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        User currentUser = null;

        // ===============================
        // LOGIN / SIGN UP
        // ===============================
        while (currentUser == null) {
            System.out.println("\n=== LOGIN MENU ===");
            System.out.println("1) Sign Up");
            System.out.println("2) Login");
            System.out.println("3) Exit");
            System.out.print("Choose: ");

            int choice;
            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Enter a number!");
                sc.nextLine();
                continue;
            }
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Username: ");
                    String u = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();

                    System.out.println(
                        UserDAO.signUp(u, p)
                            ? "Sign up successful!"
                            : "Username already exists!"
                    );
                }

                case 2 -> {
                    System.out.print("Username: ");
                    String u = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();

                    currentUser = UserDAO.login(u, p);

                    System.out.println(
                        currentUser != null
                            ? "Login successful!"
                            : "Wrong username or password!"
                    );
                }

                case 3 -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
            }
        }

        // DB → RAM
        manager.loadBooksFromDB();

        // ROLE MENU
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            adminMenu(sc);
        } else {
            userMenu(sc, currentUser);
        }

        sc.close();
    }

    // =====================================================
    // ADMIN MENU
    // =====================================================
    public static void adminMenu(Scanner sc) {

        boolean running = true;

        while (running) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1) Add Book");
            System.out.println("2) Remove Book");
            System.out.println("3) Add User");
            System.out.println("4) Remove User");
            System.out.println("5) Show Books");
            System.out.println("6) Search Book");
            System.out.println("7) Show Users");
            System.out.println("8) Exit");
            System.out.print("Choose: ");

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {

                case 1 -> {
                    boolean addMore = true;
                    while (addMore) {

                        int id = generateUniqueBookId();
                        System.out.println("Generated Book ID: " + id);

                        System.out.print("Title: ");
                        String title = sc.nextLine();

                        System.out.print("Author: ");
                        String author = sc.nextLine();

                        Book book = new Book(id, title, author);

                        if (BookDAO.addBook(book)) {
                            manager.addBook(book);
                            System.out.println("Book added.");
                        } else {
                            System.out.println("Add failed.");
                        }

                        System.out.print("Exit add book? (yes/no): ");
                        addMore = !sc.nextLine().equalsIgnoreCase("yes");
                    }
                }

                case 2 -> {
                    System.out.print("Book ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    if (BookDAO.removeBook(id)) {
                        manager.loadBooksFromDB();
                        System.out.println("Book removed.");
                    } else {
                        System.out.println("Remove failed.");
                    }
                }

                case 3 -> {
                	System.out.print("Username: ");
                    String u = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();

                    boolean ok = UserDAO.addMember(u, p, false);
                    System.out.println(ok ? "Member added ✅" : "Failed ❌");
                }

                case 4 -> {
                    System.out.print("Username: ");
                    String u = sc.nextLine();

                    System.out.println(
                        UserDAO.removeMember(u)
                            ? "User removed."
                            : "Remove failed."
                    );
                }

                case 5 -> manager.showAllBooks()
                        .forEach(System.out::println);

                case 6 -> {
                    System.out.print("Keyword: ");
                    String key = sc.nextLine();
                    manager.searchBooks(key)
                            .forEach(System.out::println);
                }

                case 7 -> UserDAO.getAllMembers()
                        .forEach(System.out::println);

                case 8 -> running = false;
            }
        }
    }

    // =====================================================
    // USER MENU
    // =====================================================
    public static void userMenu(Scanner sc, User user) {

        boolean running = true;

        while (running) {
            System.out.println("\n===== USER MENU =====");
            System.out.println("1) Search Book");
            System.out.println("2) Borrow Book");
            System.out.println("3) Return Book");
            System.out.println("4) Exit");
            System.out.print("Choose: ");

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {

                case 1 -> {
                    System.out.print("Keyword: ");
                    String key = sc.nextLine();
                    manager.searchBooks(key)
                            .forEach(System.out::println);
                }

                case 2 -> running = false;
                case 3 -> running = false;
                case 4 -> running = false;
            }
        }
    }

    // =====================================================
    // RANDOM UNIQUE BOOK ID
    // =====================================================
    public static int generateUniqueBookId() {
        int id;
        do {
            id = 10000 + random.nextInt(90000);
        } while (BookDAO.bookIdExists(id));
        return id;
    }
    
}
