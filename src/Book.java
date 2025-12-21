import java.util.Locale;

public class Book implements Seperable {

    private final int id;
    private final String title;
    private final String author;
    private boolean available = true;

    public Book(int id, String title, String author) {

        // ID kontrolü (5 haneli pozitif sayı istiyoruz)
        if (id < 10000 || id > 99999) {
            throw new IllegalArgumentException(
                "Book ID must be a 5-digit positive number"
            );
        }

        this.id = id;
        this.title = title != null ? title : "";
        this.author = author != null ? author : "";
    }

    // -------------------------
    // GETTERS
    // -------------------------
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return available;
    }

    // -------------------------
    // SETTERS
    // -------------------------
    public void setAvailable(boolean available) {
        this.available = available;
    }

    // -------------------------
    // SEARCH SUPPORT
    // -------------------------
    @Override
    public boolean matches(String key) {
        if (key == null || key.isEmpty()) return false;

        String lowerKey = key.toLowerCase(Locale.ROOT);

        return title.toLowerCase(Locale.ROOT).contains(lowerKey)
                || author.toLowerCase(Locale.ROOT).contains(lowerKey);
    }

    // -------------------------
    // TO STRING
    // -------------------------
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", available=" + available +
                '}';
    }
}
