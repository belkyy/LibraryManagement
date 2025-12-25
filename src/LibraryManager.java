import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryManager {

    private final List<Book> books = new ArrayList<>();
    private final List<Loaning> loans = new ArrayList<>();

    public void addBook(Book b) {
        if (b == null) return;
        books.add(b);
    }

    public boolean removeBookFromMemory(int id) {

        boolean isLoaned = loans.stream()
                .anyMatch(l ->
                        l.getBook().getId() == id &&
                        l.getReturnDate() == null
                );

        if (isLoaned) return false;

        return books.removeIf(b -> b.getId() == id);
    }

    public void showBooksAdmin() {

        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }

        System.out.println("============ BOOK LIST ============");

        for (Book b : books) {
            System.out.println(b.toAdminString());
        }

        System.out.println("===================================");
        System.out.println("Total books: " + books.size());
    }


    public List<Book> searchBooks(String key) {

        if (key == null || key.isEmpty())
            return Collections.emptyList();

        List<Book> result = new ArrayList<>();

        for (Book b : books) {
            if (b.matches(key)) {
                result.add(b);
            }
        }

        return Collections.unmodifiableList(result);
    }

    public boolean borrowBook(String title) {

        if (title == null || title.isEmpty())
            return false;

        boolean ok = BookDAO.borrowBookByTitle(title);

        if (ok) {
            loadBooksFromDB();
        }

        return ok;
    }


    public boolean returnBook(String title) {

        if (title == null || title.isEmpty())
            return false;

        boolean ok = BookDAO.returnBookByTitle(title);

        if (ok) {
            loadBooksFromDB();
        }

        return ok;
    }


    public void loadBooksFromDB() {
        books.clear();
        books.addAll(BookDAO.getAllBooks());
    }
}
