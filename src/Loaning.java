import java.time.LocalDate;

public class Loaning {

    private final Book book;
    private final Member member;
    private final LocalDate borrowDate;
    private LocalDate returnDate;

    public Loaning(Book book, Member member) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }

        this.book = book;
        this.member = member;
        this.borrowDate = LocalDate.now();
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean returnBook() {
        if (returnDate != null) {
            return false; // already returned
        }
        this.returnDate = LocalDate.now();
        return true;
    }

    @Override
    public String toString() {
        return "Loaning{" +
                "book=" + book.getTitle() +
                ", member=" + member.getName() +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
