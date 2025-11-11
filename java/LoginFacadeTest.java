import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

//This is the mock Authenticator implementation 
class MockAuthenticator implements Authenticator {
    @Override
    public boolean checkCredentials(String username, String password) {
        return username.equals("admin") && password.equals("1234");
    }
}

//This is the mock LoginService for LoginFacade constructor
class MockLoginService implements LoginService {
    @Override
    public boolean performLogin(String username, String password) {
        return username.equals("admin") && password.equals("1234");
    }
}

class LoginFacadeTest {

    //This is a mock class that extends the current databse connector
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
        private MockAuthenticator auth = new MockAuthenticator();

        TestableLoginFacade() {
            super(new MockLoginService()); // satisfies LoginFacade(LoginService)
        }

        @Override
        public boolean login(String username, String password) {
            mockDB.connect();
            boolean result = auth.checkCredentials(username, password);
            mockDB.disconnect();
            return result;
        }

        public MockDatabaseConnector getMockDB() { return mockDB; }
        public MockAuthenticator getAuthenticator() { return auth; }
    }

    @Test //This test has the correct credentials and should return true
    void testLoginSuccess() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertTrue(facade.login("admin", "1234"));  
    }

    @Test //This test has the wrong username and should return false
    void testLoginFailWrongUser() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("wrong", "1234")); 
    }

    @Test //This test has the wrong password and should return false
    void testLoginFailWrongPass() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("admin", "wrong")); 
    }

    @Test //This test has the wrong username and password and should return false
    void testLoginFailBothWrong() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("bad", "login"));
    }

    @Test //This test has a case-sensitive username and should return false
    void testCaseSensitivity() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("Admin", "1234")); 
    }

    @Test //This test has an empty username and should return false
    void testEmptyUsername() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("", "1234")); 
    }

    @Test //This test has an empty password and should return false
    void testEmptyPassword() {
        TestableLoginFacade facade = new TestableLoginFacade();
        assertFalse(facade.login("admin", "")); 
    }

    @Test //This test makes sure the database is disconnected after login. It should return false because it won't be connected
    void testDatabaseConnectionOpens() {
        TestableLoginFacade facade = new TestableLoginFacade();
        facade.login("admin", "1234");
        assertFalse(facade.getMockDB().isConnected()); 
    }

    @Test //This directly tests the authenticator and not the whole facade with the correct credentials
    void testAuthenticatorValidUser() {
        MockAuthenticator auth = new MockAuthenticator();
        assertTrue(auth.checkCredentials("admin", "1234")); 
    }

    @Test //This directly tests the authenticator with the incorrect credentials
    void testAuthenticatorInvalidUser() {
        MockAuthenticator auth = new MockAuthenticator();
        assertFalse(auth.checkCredentials("test", "test")); 
    }
}
