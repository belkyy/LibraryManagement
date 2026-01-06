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

	    try (Connection con = DBConnection.getConnection()) {

	        int fee = calculateLateFee(bookId, username);

	        if (fee > 0) {
	        	createFee(username, bookId, fee);
	            System.out.println(
	                "Late fee exists: " + fee + " TL. Please pay before returning the book."
	            );
	            return false;
	        }

	        String updateLoan =
	            "UPDATE loans SET return_date=NOW() " +
	            "WHERE book_id=? AND username=? AND return_date IS NULL";

	        String updateBook =
	            "UPDATE books SET available=1 WHERE id=?";

	        con.setAutoCommit(false);

	        try (PreparedStatement ps1 = con.prepareStatement(updateLoan);
	             PreparedStatement ps2 = con.prepareStatement(updateBook)) {

	            ps1.setInt(1, bookId);
	            ps1.setString(2, username);

	            if (ps1.executeUpdate() == 0) {
	                con.rollback();
	                return false;
	            }

	            ps2.setInt(1, bookId);
	            ps2.executeUpdate();

	            con.commit();
	            return true;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
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
	
	public static void getBorrowedBooksAdmin() {

	    String sql =
	        "SELECT b.title, l.username, u.role, u.is_student, " +
	        "TIMESTAMPDIFF(HOUR, l.borrow_date, NOW()) AS totalHours " +
	        "FROM loans l " +
	        "JOIN books b ON l.book_id = b.id " +
	        "JOIN users u ON l.username = u.username " +
	        "WHERE l.return_date IS NULL";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        System.out.println("\n========= BORROWED BOOKS (ADMIN) =========");

	        while (rs.next()) {

	            String title = rs.getString("title");
	            String user = rs.getString("username");
	            boolean isStudent = rs.getBoolean("is_student");
	            int totalHours = rs.getInt("totalHours");

	            int days = totalHours / 24;
	            int hours = totalHours % 24;

	            int fee = 0;
	            int lateHours = totalHours - 240;

	            if (lateHours > 0) {
	                fee = isStudent ? lateHours * 3 : lateHours * 5;
	            }

	            System.out.print(
	                "Book: " + title +
	                " | User: " + user +
	                " | Time: " + days + " days " + hours + " hours"
	            );

	            if (fee > 0) {
	                System.out.print(" | LATE FEE = " + fee + " TL");
	            }

	            System.out.println();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private static int calculateLateFee(int bookId, String username) throws SQLException {

	    String sql =
	        "SELECT u.is_student, " +
	        "TIMESTAMPDIFF(HOUR, l.borrow_date, NOW()) AS totalHours " +
	        "FROM loans l " +
	        "JOIN users u ON l.username = u.username " +
	        "WHERE l.book_id=? AND l.username=? AND l.return_date IS NULL";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, bookId);
	        ps.setString(2, username);

	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            boolean isStudent = rs.getBoolean("is_student");
	            int totalHours = rs.getInt("totalHours");

	            int lateHours = totalHours - 240;

	            if (lateHours > 0) {
	                return isStudent ? lateHours * 3 : lateHours * 5;
	            }
	        }
	    }
	    return 0;
	}
	
	private static void createFee(String username, int bookId, int amount)
	        throws SQLException {

	    String sql =
	        "INSERT INTO fees (username, book_id, amount) VALUES (?, ?, ?)";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, username);
	        ps.setInt(2, bookId);
	        ps.setInt(3, amount);
	        ps.executeUpdate();
	    }
	}
	public static boolean showUserFees(String username) {

	    String sql =
	        "SELECT f.id, b.title, f.amount " +
	        "FROM fees f JOIN books b ON f.book_id=b.id " +
	        "WHERE f.username=? AND f.paid=FALSE";

	    boolean hasFee = false;

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, username);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            hasFee = true;
	            System.out.println(
	        	     ("\n========== UNPAID FEES =========") +
	                "Fee ID: " + rs.getInt("id") +
	                " | Book: " + rs.getString("title") +
	                " | Amount: " + rs.getInt("amount") + " TL"
	            );
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    if (!hasFee) {
	        System.out.println("You have no outstanding debt.");
	    }

	    return hasFee;
	}

	public static boolean payFee(int feeId) {

	    String sql =
	        "UPDATE fees SET paid=TRUE, paid_at=NOW() WHERE id=?";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, feeId);
	        return ps.executeUpdate() > 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public static int getEstimatedLateFee(int bookId, String username) {

	    try {
	        return calculateLateFee(bookId, username);
	    } catch (SQLException e) {
	        return 0;
	    }
	}
}
