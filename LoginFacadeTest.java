import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginFacadeTest {

    // Stub classes for isolation
    class MockDatabaseConnector extends DatabaseConnector {
        private boolean connected = false;

        @Override
        public void connect() {
            connected = true;
        }

        @Override
        public void disconnect() {
            connected = false;
        }

        public boolean isConnected() {
            return connected;
        }
    }

    class TestableLoginFacade extends LoginFacade {
        private MockDatabaseConnector mockDB = new MockDatabaseConnector();
        private Authenticator auth = new Authenticator();

        @Override
        public boolean login(String username, String password) {
            mockDB.connect();
            boolean result = auth.checkCredentials(username, password);
            mockDB.disconnect();
            return result;
        }

        public MockDatabaseConnector getMockDB() { return mockDB; }
        public Authenticator getAuthenticator() { return auth; }
    }

    @Test
    void testLoginSuccess() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertTrue(facade.login("admin", "1234"));  // #1 correct credentials
    }

    @Test
    void testLoginFailWrongUser() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("wrong", "1234")); // #2 wrong username
    }

    @Test
    void testLoginFailWrongPass() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("admin", "wrong")); // #3 wrong password
    }

    @Test
    void testLoginFailBothWrong() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("bad", "login")); // #4 both wrong
    }

    @Test
    void testCaseSensitivity() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("Admin", "1234")); // #5 case-sensitive username
    }

    @Test
    void testEmptyUsername() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("", "1234")); // #6 empty username
    }

    @Test
    void testEmptyPassword() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("admin", "")); // #7 empty password
    }

    @Test
    void testDatabaseConnectionOpens() {
        TestableLoginFacade facade = new TestableLoginFacade();
        facade.login("admin", "1234");
        assertFalse(facade.getMockDB().isConnected()); // #8 connection closed after login
    }

    @Test
    void testAuthenticatorValidUser() {
        Authenticator auth = new Authenticator();
        assertTrue(auth.checkCredentials("admin", "1234")); // #9 directly test authenticator
    }

    @Test
    void testAuthenticatorInvalidUser() {
        Authenticator auth = new Authenticator();
        assertFalse(auth.checkCredentials("test", "test")); // #10 invalid login test
    }
}
