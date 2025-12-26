import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {

	public static boolean borrowBook(int bookId, String username) {

	    String insertLoan =
	        "INSERT INTO loans (book_id, username) VALUES (?, ?)";

	    String updateBook =
	        "UPDATE books SET available=0 WHERE id=? AND available=1";

	    try (Connection con = DBConnection.getConnection()) {

	        con.setAutoCommit(false);

	        try (PreparedStatement ps1 = con.prepareStatement(insertLoan);
	             PreparedStatement ps2 = con.prepareStatement(updateBook)) {

	            ps2.setInt(1, bookId);
	            if (ps2.executeUpdate() == 0) {
	                con.rollback();
	                return false;
	            }

	            ps1.setInt(1, bookId);
	            ps1.setString(2, username);
	            ps1.executeUpdate();

	            con.commit();
	            return true;
	        }

	    } catch (SQLException e) {
	        return false;
	    }
	}

	
	public static boolean returnBook(int bookId, String username) {

	    String updateLoan =
	        "UPDATE loans SET return_date=NOW() " +
	        "WHERE book_id=? AND username=? AND return_date IS NULL";

	    String updateBook =
	        "UPDATE books SET available=1 WHERE id=?";

	    try (Connection con = DBConnection.getConnection()) {

	        con.setAutoCommit(false);

	        try (PreparedStatement ps1 = con.prepareStatement(updateLoan);
	             PreparedStatement ps2 = con.prepareStatement(updateBook)) {

	            ps1.setInt(1, bookId);
	            ps1.setString(2, username);

	            if (ps1.executeUpdate() == 0) return false;

	            ps2.setInt(1, bookId);
	            ps2.executeUpdate();

	            con.commit();
	            return true;
	        }

	    } catch (SQLException e) {
	        return false;
	    }
	}

	public static List<Loaning> getUserLoans(String username) {

	    List<Loaning> list = new ArrayList<>();

	    String sql =
	        "SELECT b.id, b.title, l.username, l.borrow_date, l.return_date " +
	        "FROM loans l JOIN books b ON l.book_id=b.id " +
	        "WHERE l.username=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, username);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            list.add(new Loaning(
	                rs.getInt("id"),
	                rs.getString("title"),
	                rs.getString("username"),
	                rs.getTimestamp("borrow_date"),
	                rs.getTimestamp("return_date")
	            ));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	public static List<Loaning> getAllLoans() {

	    List<Loaning> list = new ArrayList<>();

	    String sql =
	        "SELECT b.title, l.username, l.borrow_date, l.return_date " +
	        "FROM loans l JOIN books b ON l.book_id=b.id";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            list.add(new Loaning(
	                0,
	                rs.getString("title"),
	                rs.getString("username"),
	                rs.getTimestamp("borrow_date"),
	                rs.getTimestamp("return_date")
	            ));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	public static List<Loaning> getActiveLoans(String username) {

	    List<Loaning> list = new ArrayList<>();

	    String sql =
	        "SELECT b.id, b.title, l.borrow_date " +
	        "FROM loans l JOIN books b ON l.book_id=b.id " +
	        "WHERE l.username=? AND l.return_date IS NULL";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, username);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            list.add(new Loaning(
	                rs.getInt("id"),
	                rs.getString("title"),
	                username,
	                rs.getTimestamp("borrow_date"),
	                null
	            ));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}

}
