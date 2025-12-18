import java.util.Locale;

public class Book implements Seperable {

    private final String id;
    private final String title;
    private final String author;
    private boolean available = true;

    public Book(String id, String title, String author) {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException("Book ID cannot be null or empty");

        this.id = id;
        this.title = title != null ? title : "";
        this.author = author != null ? author : "";
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return available; }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    @Override
    public boolean matches(String key) {
        if (key == null || key.isEmpty()) return false;

        String lowerKey = key.toLowerCase(Locale.ROOT);
        return title.toLowerCase(Locale.ROOT).contains(lowerKey)
                || author.toLowerCase(Locale.ROOT).contains(lowerKey);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", available=" + available +
                '}';
    }
}
