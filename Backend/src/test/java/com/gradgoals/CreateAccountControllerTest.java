package com.gradgoals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateAccountControllerTest {

    @Mock
    private SupabaseClient supabaseClient;

    @InjectMocks
    private CreateAccountController createAccountController;

    //Tests createAccount() validations

    @Test
    void testCreateAccount_Success() {
        //Simulates a valid new user who does not exist yet
        when(supabaseClient.getPasswordForUser("newUser")).thenReturn(null);
        when(supabaseClient.createUser("newUser", "password123")).thenReturn(true);

        LoginRequest request = new LoginRequest("newUser", "password123");
        ResponseEntity<Map<String, String>> response = createAccountController.createAccount(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Account created successfully!", response.getBody().get("message"));
    }

    @Test
    void testCreateAccount_NullUsername() {
        //Ensures API rejects null username immediately
        LoginRequest request = new LoginRequest(null, "password123");
        ResponseEntity<Map<String, String>> response = createAccountController.createAccount(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username and password cannot be empty.", response.getBody().get("message"));
    }

    @Test
    void testCreateAccount_EmptyUsername() {
        //Ensures API rejects empty string username
        LoginRequest request = new LoginRequest("", "password123");
        ResponseEntity<Map<String, String>> response = createAccountController.createAccount(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateAccount_NullPassword() {
        //Ensures API rejects null password
        LoginRequest request = new LoginRequest("validUser", null);
        ResponseEntity<Map<String, String>> response = createAccountController.createAccount(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateAccount_EmptyPassword() {
        //Ensures API rejects empty string password
        LoginRequest request = new LoginRequest("validUser", "");
        ResponseEntity<Map<String, String>> response = createAccountController.createAccount(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateAccount_WhitespaceOnly() {
        //Tests that the trim() function works by rejecting strings with only spaces
        LoginRequest request = new LoginRequest("   ", "   ");
        ResponseEntity<Map<String, String>> response = createAccountController.createAccount(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    //Tests createAccount() database interactions

    @Test
    void testCreateAccount_UserAlreadyExists() {
        //Simulates scenario where user tries to register a name that is taken
        when(supabaseClient.getPasswordForUser("existingUser")).thenReturn("somePassword");

        LoginRequest request = new LoginRequest("existingUser", "newPass");
        ResponseEntity<Map<String, String>> response = createAccountController.createAccount(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists. Please choose a different username.", response.getBody().get("message"));
    }

    @Test
    void testCreateAccount_DatabaseCreationFailure() {
        //Simulates Supabase returning false (failed to insert row)
        when(supabaseClient.getPasswordForUser("newUser")).thenReturn(null);
        when(supabaseClient.createUser("newUser", "password123")).thenReturn(false);

        LoginRequest request = new LoginRequest("newUser", "password123");
        ResponseEntity<Map<String, String>> response = createAccountController.createAccount(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to create account. Database error.", response.getBody().get("message"));
    }

    @Test
    void testCreateAccount_ExceptionDuringCheck() {
        //Simulates DB crashing while checking if user exists
        when(supabaseClient.getPasswordForUser("errorUser")).thenThrow(new RuntimeException("DB Connection Fail"));

        LoginRequest request = new LoginRequest("errorUser", "pass");

        assertThrows(RuntimeException.class, () -> {
            createAccountController.createAccount(request);
        });
    }

    @Test
    void testCreateAccount_ExceptionDuringCreation() {
        //Simulates DB crashing specifically during the creation step
        when(supabaseClient.getPasswordForUser("newUser")).thenReturn(null);
        when(supabaseClient.createUser("newUser", "pass")).thenThrow(new RuntimeException("Insert Fail"));

        LoginRequest request = new LoginRequest("newUser", "pass");

        assertThrows(RuntimeException.class, () -> {
            createAccountController.createAccount(request);
        });
    }
}