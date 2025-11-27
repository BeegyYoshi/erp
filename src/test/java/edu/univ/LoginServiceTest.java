import edu.univ.erp.auth.LoginResult;
import edu.univ.erp.auth.LoginService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {

    @Test
    void testAdminLoginSuccess() {
        LoginResult result = LoginService.login("admin1", "admin123");
        assertTrue(result.ok);
        assertEquals("admin", result.role);
    }

    @Test
    void testLoginFail() {
        LoginResult result = LoginService.login("admin1", "wrongpass");
        assertFalse(result.ok);
    }

    @Test
    void testWrongUsernameLogin() {
        LoginResult result = LoginService.login("notauser", "admin123");
        assertFalse(result.ok);
    }
}
