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

    public List<Book> showAllBooks() {
        return Collections.unmodifiableList(books);
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

    public boolean borrowBook(int bookId, Member member) {

        if (member == null) return false;

        for (Book b : books) {
            if (b.getId() == bookId && b.isAvailable()) {
                b.setAvailable(false);
                loans.add(new Loaning(b, member));
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(int bookId) {

        for (Loaning loan : loans) {
            if (loan.getBook().getId() == bookId &&
                loan.getReturnDate() == null) {

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
