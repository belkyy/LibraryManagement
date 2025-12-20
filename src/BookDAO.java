import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // -------------------------
    // ADD BOOK (ADMIN)
    // -------------------------
    public static boolean addBook(Book book) {

        String sql =
            "INSERT INTO books (id, title, author, available) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setBoolean(4, book.isAvailable());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // -------------------------
    // GET ALL BOOKS
    // -------------------------
    public static List<Book> getAllBooks() {

        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Book b = new Book(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author")
                );
                b.setAvailable(rs.getBoolean("available"));
                books.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }
    
    // -----------------
    // Random ID maker
    // -----------------
    
    public static boolean bookIdExists(String id) {

        String sql = "SELECT id FROM books WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
