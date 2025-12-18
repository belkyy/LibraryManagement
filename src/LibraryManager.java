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

    public boolean removeBook(String id) {
        if (id == null) return false;

        // Eğer kitap ödünçteyse silinmesini engelle
        boolean isLoaned = loans.stream()
                .anyMatch(l -> l.getBook().getId().equals(id) && l.getReturnDate() == null);

        if (isLoaned) {
            return false; // Ödünçte olan kitap silinemez
        }

        return books.removeIf(b -> b.getId().equals(id));
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

        // dışarı mutable liste vermiyoruz
        return Collections.unmodifiableList(result);
    }

    public boolean borrowBook(String bookId, Member member) {
        if (bookId == null || member == null) return false;

        for (Book b : books) {

            // Kitap bulundu
            if (b.getId().equals(bookId)) {

                if (!b.isAvailable()) {
                    return false; // Zaten ödünçte
                }

                b.setAvailable(false);
                loans.add(new Loaning(b, member));
                return true;
            }
        }

        return false; // Kitap bulunamadı
    }

    public boolean returnBook(String bookId) {
        if (bookId == null) return false;

        for (Loaning loan : loans) {
            if (loan.getBook().getId().equals(bookId) && loan.getReturnDate() == null) {

                loan.returnBook();
                loan.getBook().setAvailable(true);

                return true;
            }
        }
        return false;
    }
}
