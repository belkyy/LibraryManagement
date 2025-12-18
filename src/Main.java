import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // -------------------------------
        // LOGIN (SQL BASED)
        // -------------------------------
        User currentUser = null;

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
                System.out.print("Create Username: ");
                String u = sc.nextLine();
                System.out.print("Create Password: ");
                String p = sc.nextLine();

                boolean success = UserDAO.signUp(u, p);
                if (success) {
                    System.out.println("Sign up successful! You can login now.");
                } else {
                    System.out.println("Sign up failed!");
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
            } else {
                System.out.println("Invalid option!");
            }
        }


        // -------------------------------
        // LIBRARY SYSTEM
        // -------------------------------
        LibraryManager manager = new LibraryManager();
        ArrayList<Member> members = new ArrayList<>();

        boolean running = true;

        while (running) {

            System.out.println("\n===== LIBRARY MENU =====");
            System.out.println("1) Add Book");
            System.out.println("2) Add Member");
            System.out.println("3) Show Books");
            System.out.println("4) Search Book");
            System.out.println("5) Borrow Book");
            System.out.println("6) Return Book");
            System.out.println("7) Show Members");
            System.out.println("8) Exit");
            System.out.print("Choose: ");

            int option;
            try {
                option = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Enter a number!");
                sc.nextLine();
                continue;
            }
            sc.nextLine();

            switch (option) {

                // -------------------------
                // ADD BOOK
                // -------------------------
                case 1:
                    System.out.print("Book ID: ");
                    String id = sc.nextLine();

                    System.out.print("Title: ");
                    String title = sc.nextLine();

                    System.out.print("Author: ");
                    String author = sc.nextLine();

                    manager.addBook(new Book(id, title, author));
                    System.out.println("Book added!");
                    break;

                // -------------------------
                // ADD MEMBER
                // -------------------------
                case 2:
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
                    System.out.println("Member added!");
                    break;

                // -------------------------
                // SHOW BOOKS
                // -------------------------
                case 3:
                    System.out.println("\n--- BOOKS ---");
                    for (Book b : manager.searchBooks("")) {
                        System.out.println(
                                b.getId() + " | " +
                                b.getTitle() + " | " +
                                b.getAuthor() + " | " +
                                (b.isAvailable() ? "Available" : "Borrowed")
                        );
                    }
                    break;

                // -------------------------
                // SEARCH BOOK
                // -------------------------
                case 4:
                    System.out.print("Keyword: ");
                    String key = sc.nextLine();

                    System.out.println("\n--- RESULTS ---");
                    for (Book b : manager.searchBooks(key)) {
                        System.out.println(b.getId() + " | " + b.getTitle());
                    }
                    break;

                // -------------------------
                // BORROW BOOK
                // -------------------------
                case 5:
                    System.out.print("Book ID: ");
                    String bid = sc.nextLine();

                    System.out.print("Member ID: ");
                    String memId = sc.nextLine();

                    Member borrower = null;
                    for (Member mem : members) {
                        if (mem.getID().equals(memId)) {
                            borrower = mem;
                            break;
                        }
                    }

                    if (borrower == null) {
                        System.out.println("Member not found!");
                    } else if (manager.borrowBook(bid, borrower)) {
                        System.out.println("Book borrowed!");
                    } else {
                        System.out.println("Borrow failed.");
                    }
                    break;

                // -------------------------
                // RETURN BOOK
                // -------------------------
                case 6:
                    System.out.print("Book ID: ");
                    String rid = sc.nextLine();

                    if (manager.returnBook(rid)) {
                        System.out.println("Book returned!");
                    } else {
                        System.out.println("Return failed.");
                    }
                    break;

                // -------------------------
                // SHOW MEMBERS
                // -------------------------
                case 7:
                    System.out.println("\n--- MEMBERS ---");
                    for (Member mem : members) {
                        System.out.println(
                                mem.getID() + " | " +
                                mem.getName() +
                                (mem instanceof StudentMember ? " (Student)" : "")
                        );
                    }
                    break;

                // -------------------------
                // EXIT
                // -------------------------
                case 8:
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }

        sc.close();
    }
}
