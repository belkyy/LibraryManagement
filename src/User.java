import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class User {

    private final String username;
    private final String passwordHash; // hashlenmiş parola

    public User(String username, String password) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");

        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password cannot be empty");

        this.username = username;
        this.passwordHash = hash(password);
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String pw) {
        if (pw == null) return false;
        return passwordHash.equals(hash(pw));
    }

    private String hash(String input) {
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
        return "User{username='" + username + "'}";
    }
}
