import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class User {

    private final String username;
    private final String passwordHash; // DB'deki hash
    private final String role;          // ADMIN / USER
    private boolean student;            // 🔥 final KALDIRILDI

    // 🔹 DB'den okurken kullanılır
    public User(String username, String passwordHash, String role) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");

        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.student = false; // default
    }

    // 🔹 Yeni kullanıcı oluştururken (SIGN UP / ADD MEMBER)
    public static User createNew(
            String username,
            String plainPassword,
            String role,
            boolean student
    ) {
        User u = new User(username, hash(plainPassword), role);
        u.student = student;
        return u;
    }

    // =========================
    // GETTERS / SETTERS
    // =========================
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isStudent() {
        return student;
    }

    public void setStudent(boolean student) {
        this.student = student;
    }

    // 🔐 password kontrolü
    public boolean checkPassword(String inputPassword) {
        if (inputPassword == null) return false;
        return passwordHash.equals(hash(inputPassword));
    }

    // DAO için hash getter (reflection yok!)
    String getPasswordHash() {
        return passwordHash;
    }

    // =========================
    // HASH
    // =========================
    private static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    @Override
    public String toString() {
        return username +
                (student ? " (Student)" : "") +
                " [" + role + "]";
    }
}
