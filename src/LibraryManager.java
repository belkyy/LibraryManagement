import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryManager {

    private final List<Book> books = new ArrayList<>();
    private final List<Loaning> loans = new ArrayList<>();

    // -------------------------
    // ADD BOOK
    // -------------------------
    public void addBook(Book b) {
        if (b == null) return;
        books.add(b);
    }

    // -------------------------
    // REMOVE BOOK (ADMIN)
    // -------------------------
    public boolean removeBook(String id) {
        if (id == null) return false;

        // Ödünçteyse silme
        boolean isLoaned = loans.stream()
                .anyMatch(l -> l.getBook().getId().equals(id)
                        && l.getReturnDate() == null);

        if (isLoaned) return false;

        return books.removeIf(b -> b.getId().equals(id));
    }

    // -------------------------
    // SHOW ALL BOOKS ⭐ (EKLENEN)
    // -------------------------
    public List<Book> showAllBooks() {
        // dışarıdan değiştirilemesin
        return Collections.unmodifiableList(books);
    }

    // -------------------------
    // SEARCH BOOK
    // -------------------------
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

    // -------------------------
    // BORROW BOOK (USER)
    // -------------------------
    public boolean borrowBook(String bookId, Member member) {
        if (bookId == null || member == null) return false;

        for (Book b : books) {
            if (b.getId().equals(bookId)) {

                if (!b.isAvailable()) {
                    return false;
                }

                b.setAvailable(false);
                loans.add(new Loaning(b, member));
                return true;
            }
        }
        return false;
    }

    // -------------------------
    // RETURN BOOK (USER)
    // -------------------------
    public boolean returnBook(String bookId) {
        if (bookId == null) return false;

        for (Loaning loan : loans) {
            if (loan.getBook().getId().equals(bookId)
                    && loan.getReturnDate() == null) {

                loan.returnBook();
                loan.getBook().setAvailable(true);
                return true;
            }
        }
        return false;
    }
    public void loadBooksFromDB() {
        books.clear();
        books.addAll(BookDAO.getAllBooks());
    }
}
