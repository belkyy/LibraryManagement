import java.sql.Timestamp;

public class Loaning {

    private int bookId;
    private String bookTitle;
    private String username;
    private Timestamp borrowDate;
    private Timestamp returnDate;

    public Loaning(int bookId, String bookTitle, String username,
                Timestamp borrowDate, Timestamp returnDate) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.username = username;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    @Override
    public String toString() {
        return bookTitle +
               " | User: " + username +
               " | Borrowed: " + borrowDate +
               " | " + (isReturned() ? "Returned" : "Still Borrowed");
    }
}
