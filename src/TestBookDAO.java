import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestBookDAO {

    private int validBookId;
    private int invalidBookId;

    @BeforeEach
    void setUp() {
        validBookId = 20482;   // DB’de GERÇEKTEN olan bir kitap ID
        invalidBookId = 99999; // DB’de OLMAYAN kitap
    }

    @Test
    void bookIdExists_validId_shouldReturnTrue() {
        boolean exists = BookDAO.bookIdExists(validBookId);
        assertTrue(exists);
    }

    @Test
    void bookIdExists_invalidId_shouldReturnFalse() {
        boolean exists = BookDAO.bookIdExists(invalidBookId);
        assertFalse(exists);
    }

    @Test
    void getBookById_validId_shouldNotBeNull() {
        Book book = BookDAO.getBookById(validBookId);
        assertNotNull(book);
    }

    @Test
    void getBookById_invalidId_shouldBeNull() {
        Book book = BookDAO.getBookById(invalidBookId);
        assertNull(book);
    }
}
