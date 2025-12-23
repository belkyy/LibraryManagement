import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public static boolean addBook(Book book) {

        String sql =
            "INSERT INTO books (id, title, author, available) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, book.getId());          // 🔥 INT
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

    public static List<Book> getAllBooks() {

        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Book b = new Book(
                        rs.getInt("id"),          // 🔥 INT
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

    public static boolean bookIdExists(int id) {

        String sql = "SELECT id FROM books WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean removeBook(int bookId) {

        String checkSql =
            "SELECT available FROM books WHERE id = ?";

        String deleteSql =
            "DELETE FROM books WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {

            // Kitap var mı + ödünçte mi?
            try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                checkPs.setInt(1, bookId);
                ResultSet rs = checkPs.executeQuery();

                if (!rs.next()) {
                    return false; // kitap yok
                }

                boolean available = rs.getBoolean("available");
                if (!available) {
                    return false; // ödünçte → silinemez
                }
            }

            // Silme
            try (PreparedStatement delPs = con.prepareStatement(deleteSql)) {
                delPs.setInt(1, bookId);
                return delPs.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
