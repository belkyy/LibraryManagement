import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static LibraryManager manager = new LibraryManager();
    static ArrayList<Member> members = new ArrayList<>();
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

            if (choice == 1) {
                System.out.print("Username: ");
                String u = sc.nextLine();
                System.out.print("Password: ");
                String p = sc.nextLine();

                if (UserDAO.signUp(u, p)) {
                    System.out.println("Sign up successful!");
                } else {
                    System.out.println("Username already exists!");
                }

            } else if (choice == 2) {
                System.out.print("Username: ");
                String u = sc.nextLine();
                System.out.print("Password: ");
                String p = sc.nextLine();

                currentUser = UserDAO.login(u, p);

                if (currentUser != null) {
                    System.out.println("Login successful!");
                } else {
                    System.out.println("Wrong username or password!");
                }

            } else if (choice == 3) {
                System.out.println("Goodbye!");
                System.exit(0);
            }
        }

        // ===============================
        // ROLE BASED MENU
        // ===============================
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            adminMenu(sc);
        } else {
            userMenu(sc);
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
            System.out.println("3) Add Member");
            System.out.println("4) Remove Member");
            System.out.println("5) Show Books");
            System.out.println("6) Search Book");
            System.out.println("7) Show Members");
            System.out.println("8) Exit");
            System.out.print("Choose: ");

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {

            case 1 -> {

                boolean addMore = true;

                while (addMore) {

                    String id = generateUniqueBookId();
                    System.out.println("Generated Book ID: " + id);

                    System.out.print("Title: ");
                    String title = sc.nextLine();

                    System.out.print("Author: ");
                    String author = sc.nextLine();

                    Book book = new Book(id, title, author);

                    if (BookDAO.addBook(book)) {
                        manager.addBook(book);
                        System.out.println("Book added successfully.");
                    } else {
                        System.out.println("Failed to add book.");
                    }

                    System.out.print("Exit add book? (yes/no): ");
                    String answer = sc.nextLine();

                    if (answer.equalsIgnoreCase("yes")) {
                        addMore = false;
                    }
                }
            }


                case 2 -> {
                    System.out.print("Book ID: ");
                    String id = sc.nextLine();
                    System.out.println(
                            manager.removeBook(id)
                                    ? "Book removed."
                                    : "Remove failed."
                    );
                }

                case 3 -> {
                    System.out.print("Member ID: ");
                    String mid = sc.nextLine();
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Student (yes/no): ");
                    String s = sc.nextLine();

                    Member m = s.equalsIgnoreCase("yes")
                            ? new StudentMember(name, mid)
                            : new Member(name, mid);

                    members.add(m);
                    System.out.println("Member added.");
                }

                case 4 -> {
                    System.out.print("Member ID: ");
                    String id = sc.nextLine();
                    members.removeIf(m -> m.getID().equals(id));
                    System.out.println("Member removed.");
                }

                case 5 -> {
                    System.out.println("\n--- BOOKS ---");
                    manager.showAllBooks().forEach(b ->
                            System.out.println(
                                    b.getId() + " | " +
                                    b.getTitle() + " | " +
                                    b.getAuthor() + " | " +
                                    (b.isAvailable() ? "Available" : "Borrowed")
                            )
                    );
                }

                case 6 -> {
                    System.out.print("Keyword: ");
                    String key = sc.nextLine();
                    manager.searchBooks(key).forEach(b ->
                            System.out.println(
                                    b.getId() + " | " + b.getTitle()
                            )
                    );
                }

                case 7 -> members.forEach(m ->
                        System.out.println(
                                m.getID() + " | " +
                                m.getName() +
                                (m instanceof StudentMember ? " (Student)" : "")
                        )
                );

                case 8 -> running = false;
            }
        }
    }

    // =====================================================
    // USER MENU
    // =====================================================
    public static void userMenu(Scanner sc) {

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
                    manager.searchBooks(key).forEach(b ->
                            System.out.println(
                                    b.getId() + " | " + b.getTitle()
                            )
                    );
                }

                case 2 -> {
                    System.out.print("Book ID: ");
                    String bid = sc.nextLine();
                    System.out.print("Member ID: ");
                    String mid = sc.nextLine();

                    Member m = members.stream()
                            .filter(mem -> mem.getID().equals(mid))
                            .findFirst()
                            .orElse(null);

                    System.out.println(
                            m != null && manager.borrowBook(bid, m)
                                    ? "Book borrowed."
                                    : "Borrow failed."
                    );
                }

                case 3 -> {
                    System.out.print("Book ID: ");
                    String id = sc.nextLine();
                    System.out.println(
                            manager.returnBook(id)
                                    ? "Book returned."
                                    : "Return failed."
                    );
                }

                case 4 -> running = false;
            }
        }
    }

    // =====================================================
    // RANDOM 5 DIGIT BOOK ID (DB CHECK)
    // =====================================================
    public static String generateUniqueBookId() {
        Random r = new Random();
        String id;

        do {
            id = String.valueOf(10000 + r.nextInt(90000));
        } while (BookDAO.bookIdExists(id)); // DB kontrolü

        return id;
    }

}
