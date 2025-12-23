import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static User login(String username, String password) {

        String sql =
            "SELECT username, password, role, is_student " +
            "FROM users WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                User user = new User(
                        rs.getString("username"),
                        rs.getString("password"), // HASH
                        rs.getString("role")
                );

                user.setStudent(rs.getBoolean("is_student"));

                if (user.checkPassword(password)) {
                    return user;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean signUp(String username, String password, boolean isStudent) {

        String sql =
            "INSERT INTO users (username, password, role, is_student) " +
            "VALUES (?, ?, 'USER', false)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            User u = User.createNew(username, password, "USER", isStudent);

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setBoolean(3, isStudent);
            
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false; // username unique ihlali
        }
    }

    public static boolean addMember(
            String username,
            String password,
            boolean isStudent
    ) {

        String sql =
            "INSERT INTO users (username, password, role, is_student) " +
            "VALUES (?, ?, 'USER', ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            User u = User.createNew(username, password, "USER", isStudent);

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setBoolean(3, isStudent);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean removeMember(String username) {

        String sql =
            "DELETE FROM users WHERE username=? AND role='USER'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    public static List<User> getAllMembers() {

        List<User> users = new ArrayList<>();

        String sql =
            "SELECT username, password, role, is_student " +
            "FROM users WHERE role='USER'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                User u = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );

                u.setStudent(rs.getBoolean("is_student"));
                users.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
    
}
