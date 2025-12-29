import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestUserDAO {

    private String username;
    private String password;
    private String wrongPassword;

    @BeforeEach
    void setUp() {
        // DB'de OLMAYAN random user
        username = "test_user_" + System.currentTimeMillis();
        password = "1234";
        wrongPassword = "wrong";
    }

    @Test
    void signUp_newUser_shouldReturnTrue() {
        boolean result = UserDAO.signUp(username, password, false);
        assertTrue(result);
    }

    @Test
    void signUp_duplicateUser_shouldReturnFalse() {
        UserDAO.signUp(username, password, false);
        boolean secondTry = UserDAO.signUp(username, password, false);
        assertFalse(secondTry);
    }

    @Test
    void login_correctCredentials_shouldReturnUser() {
        UserDAO.signUp(username, password, false);
        User user = UserDAO.login(username, password);
        assertNotNull(user);
        assertEquals(username, user.getUsername());
    }

    @Test
    void login_wrongPassword_shouldReturnNull() {
        UserDAO.signUp(username, password, false);
        User user = UserDAO.login(username, wrongPassword);
        assertNull(user);
    }

    @Test
    void removeMember_existingUser_shouldReturnTrue() {
        UserDAO.signUp(username, password, false);
        boolean removed = UserDAO.removeMember(username);
        assertTrue(removed);
    }

    @Test
    void removeMember_nonExistingUser_shouldReturnFalse() {
        boolean removed = UserDAO.removeMember("no_such_user");
        assertFalse(removed);
    }
}
