import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.List;

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
            System.out.println("\n======= LOGIN MENU =======");
            System.out.println("1) Sign Up");
            System.out.println("2) Login");
            System.out.println("3) Exit");
            System.out.println("==========================");
            System.out.print("Choose: ");

            int choice;
            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please, Enter a valid number!");
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

                String ans;
                boolean isStudent;
                do {
                    System.out.print("Are you a student? (yes/no): ");
                    ans = sc.nextLine().trim().toLowerCase();
                    if (!ans.equals("yes") && !ans.equals("no")) {
                        System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                    }
                } while (!ans.equals("yes") && !ans.equals("no"));
                isStudent = ans.equals("yes");

                boolean ok = UserDAO.signUp(u, p, isStudent);

                System.out.println(
                    ok
                        ? "Sign up successful!"
                        : "Username already exists!"
                );
            }
                case 2 -> {
                    System.out.print("Username: ");
                    String u = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();

                    try {
                        currentUser = UserDAO.login(u, p);
                        System.out.println(
                                currentUser != null
                                        ? "Login successful!"
                                        : "Login failed: Wrong username or password."
                        );
                    } catch (Exception e) {
                        System.out.println("Error during login: " + e.getMessage());
                    }
                }

                case 3 -> {
                    System.out.println("Goodbye, Thanks for visiting!");
                    sc.close();
                    System.exit(0);
                }

                default -> System.out.println("Invalid option. Choose 1-3.");
            }
        }

        try {
            manager.loadBooksFromDB();
        } catch (Exception e) {
            System.out.println("Error loading books from database: " + e.getMessage());
        }

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
            System.out.println("\n=========== ADMIN MENU ===========");
            System.out.println("1) Add Book");
            System.out.println("2) Remove Book");
            System.out.println("3) Add User");
            System.out.println("4) Remove User");
            System.out.println("5) Show Books");
            System.out.println("6) Show Borrowed Books");
            System.out.println("7) Search Book");
            System.out.println("8) Show Users");
            System.out.println("9) Exit");
            System.out.println("==================================");
            System.out.print("Choose: ");

            int option;
            try {
                option = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Enter a valid number.");
                sc.nextLine();
                continue;
            }
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

                        try {
                            Book book = new Book(id, title, author);

                            if (BookDAO.addBook(book)) {
                                manager.addBook(book);
                                System.out.println("Book added.");
                            } else {
                                System.out.println("Failed to add book.");
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                        System.out.print("Exit add book? (yes/no): ");
                        addMore = !sc.nextLine().equalsIgnoreCase("yes");
                    }
                }

                case 2 -> {
                    try {
                        System.out.print("Book ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        if (!BookDAO.bookIdExists(id)) {
                            throw new IllegalArgumentException("Book ID " + id + " does not exist.");
                        }

                        if (BookDAO.removeBook(id)) {
                            manager.loadBooksFromDB();
                            System.out.println("Book removed successfully.");
                        } else {
                            System.out.println("Remove failed. Book may be currently borrowed.");
                        }

                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input! Please enter a valid integer Book ID.");
                        sc.nextLine(); 
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                    }
                }

                case 3 -> {
                    System.out.print("Username: ");
                    String u = sc.nextLine();
                    System.out.print("Password: ");
                    String p = sc.nextLine();
                    System.out.print("Is student? (yes/no): ");
                    String ans = sc.nextLine();
                    boolean isStudent = ans.equalsIgnoreCase("yes");

                    if (UserDAO.addMember(u, p, isStudent)) {
                        System.out.println("Member added successfully!");
                    } else {
                        System.out.println("Failed to add member. Username may already exist.");
                    }
                }

                case 4 -> {
                    System.out.print("Username to remove: ");
                    String username = sc.nextLine();
                    System.out.print("Are you sure? (yes/no): ");
                    String confirm = sc.nextLine();
                    if (!confirm.equalsIgnoreCase("yes")) {
                        System.out.println("Operation cancelled.");
                        break;
                    }

                    if (UserDAO.removeMember(username)) {
                        System.out.println("Member removed successfully!");
                    } else {
                        System.out.println("Failed to remove member. User not found or is admin.");
                    }
                }

                case 5 -> manager.showBooksAdmin();

                case 6 -> LoanDAO.getBorrowedBooksAdmin();

                case 7 -> {
                    System.out.print("Keyword: ");
                    String key = sc.nextLine();
                    manager.searchBooks(key).forEach(System.out::println);
                }

                case 8 -> {
                    List<User> users = UserDAO.getAllMembers();
                    if (users.isEmpty()) {
                        System.out.println("No users found.");
                    } else {
                        System.out.println("\n======== USERS ========");
                        for (User u : users) {
                            System.out.println("- " + u.getUsername() +
                                    " [" + u.getRole() + "]" +
                                    (u.isStudent() ? " (Student)" : ""));
                        }
                        System.out.println("=======================");
                        System.out.println("Total user count = " + users.size());
                    }
                }

                case 9 -> running = false;

                default -> System.out.println("Invalid option. Choose 1-9.");
            }
        }
    }

    // =====================================================
    // USER MENU
    // =====================================================
    public static void userMenu(Scanner sc, User user) {
        boolean running = true;

        while (running) {
            System.out.println("\n=========== USER MENU ===========");
            System.out.println("1) Search Book");
            System.out.println("2) Borrow Book");
            System.out.println("3) Return Book");
            System.out.println("4) View / Pay Fees");
            System.out.println("5) Exit");
            System.out.println("=================================");
            System.out.print("Choose: ");

            int option;
            try {
                option = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Enter a valid number.");
                sc.nextLine();
                continue;
            }
            sc.nextLine();

            switch (option) {
                case 1 -> {
                    System.out.print("Keyword: ");
                    String key = sc.nextLine();
                    manager.searchBooks(key).forEach(System.out::println);
                }

                case 2 -> {
                    List<Book> availableBooks = BookDAO.getAvailableBooks();
                    if (availableBooks.isEmpty()) {
                        System.out.println("No available books at the moment.");
                        break;
                    }

                    System.out.println("\n========= AVAILABLE BOOKS =========");
                    for (Book b : availableBooks) {
                        System.out.println(
                                b.getId() + " - " + b.getTitle() + " (" + b.getAuthor() + ")"
                        );
                    }

                    System.out.print("\nEnter Book ID to borrow: ");
                    try {
                        int bookId = sc.nextInt();
                        sc.nextLine();
                        if (LoanDAO.borrowBook(bookId, user.getUsername())) {
                            System.out.println("Book borrowed successfully!");
                        } else {
                            System.out.println("Borrow failed. Book may already be borrowed or invalid ID.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid book ID input.");
                        sc.nextLine();
                    }
                }

                case 3 -> {
                    while (true) {
                        List<Loaning> myLoans = LoanDAO.getActiveLoans(user.getUsername());
                        if (myLoans.isEmpty()) {
                            System.out.println("You have no borrowed books.");
                            break;
                        }

                        System.out.println("\n========= YOUR BORROWED BOOKS =========");
                        for (Loaning l : myLoans) {
                            System.out.println(l.getBookId() + " - " + l.getBookTitle() +
                                    " | Borrowed: " + l.getBorrowDate());
                        }

                        System.out.print("\nEnter BOOK ID to return: ");
                        try {
                            int bookId = Integer.parseInt(sc.nextLine());
                            if (LoanDAO.returnBook(bookId, user.getUsername())) {
                                System.out.println("Book returned successfully.");
                            } else {
                                System.out.println("Return failed. Check book ID or unpaid fees.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                        }

                        System.out.print("\nReturn another book? (yes/no): ");
                        if (!sc.nextLine().equalsIgnoreCase("yes")) {
                            break;
                        }
                    }
                }

                case 4 -> {

                    List<Loaning> activeLoans =
                        LoanDAO.getActiveLoans(user.getUsername());

                    if (activeLoans.isEmpty()) {
                        System.out.println("You have no borrowed books.");
                        break;
                    }

                    boolean hasLateFee = false;

                    System.out.println("\n====== ESTIMATED LATE FEES ======");

                    for (Loaning l : activeLoans) {

                        int fee = LoanDAO.getEstimatedLateFee(
                            l.getBookId(),
                            user.getUsername()
                        );

                        if (fee > 0) {
                            hasLateFee = true;
                            System.out.println(
                                "Book: " + l.getBookTitle() + 
                                " | Late Fee: " + fee + " TL"
                            );
                        }
                    }

                    if (!hasLateFee) {
                        System.out.println("You have no outstanding debt.");
                    }

                    break;
                }

                case 5 -> running = false;

                default -> System.out.println("Invalid option. Choose 1-5.");
            }
        }
    }

    public static int generateUniqueBookId() {
        int id;
        do {
            id = 10000 + random.nextInt(90000);
        } while (BookDAO.bookIdExists(id));
        return id;
    }
}
