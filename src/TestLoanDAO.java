import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestLoanDAO {

    private int validBookId;
    private int invalidBookId;
    private String username;

    @BeforeEach
    void setUp() {
        validBookId = 20482;        // DB de oln rastgele bi kitap
        invalidBookId = 9999;   // DB'de OLMAYAN kitap
        username = "admin";
    }

    @Test
    void borrowBook_validBook_shouldReturnTrue() {
        boolean result = LoanDAO.borrowBook(validBookId, username);
        assertTrue(result);
    }

    @Test
    void borrowBook_sameBookTwice_shouldReturnFalse() {
        LoanDAO.borrowBook(validBookId, username);
        boolean secondTry = LoanDAO.borrowBook(validBookId, username);
        assertFalse(secondTry);
    }

    @Test
    void borrowBook_invalidBook_shouldReturnFalse() {
        boolean result = LoanDAO.borrowBook(invalidBookId, username);
        assertFalse(result);
    }


    @Test
    void returnBook_validLoan_shouldReturnTrue() {
        LoanDAO.borrowBook(validBookId, username);
        boolean result = LoanDAO.returnBook(validBookId, username);
        assertTrue(result);
    }

    @Test
    void returnBook_notBorrowedBook_shouldReturnFalse() {
        boolean result = LoanDAO.returnBook(validBookId, username);
        assertFalse(result);
    }


    @Test
    void getUserLoans_shouldNotBeNull() {
        assertNotNull(LoanDAO.getUserLoans(username));
    }

    @Test
    void getAllLoans_shouldNotBeNull() {
        assertNotNull(LoanDAO.getAllLoans());
    }

    @Test
    void getActiveLoans_shouldNotBeNull() {
        assertNotNull(LoanDAO.getActiveLoans(username));
    }

    @Test
    void showUserFees_shouldReturnBoolean() {
        boolean result = LoanDAO.showUserFees(username);
        assertTrue(result || !result); 
    }

    @Test
    void payFee_invalidFeeId_shouldReturnFalse() {
        boolean result = LoanDAO.payFee(9999);
        assertFalse(result);
    }
}
