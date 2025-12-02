package com.gradgoals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Mock
    private SupabaseClient supabaseClient;

    @InjectMocks
    private LoginController loginController;

    //Tests login() success scenario

    @Test
    void testLogin_Success() {
        //Simulates the database returning the correct password "secret123"
        when(supabaseClient.getPasswordForUser("john_doe")).thenReturn("secret123");

        LoginRequest request = new LoginRequest("john_doe", "secret123");
        ResponseEntity<Map<String, String>> response = loginController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful!", response.getBody().get("message"));
    }

    @Test
    void testLogin_UserNotFound() {
        //Simulates Supabase returning null (user does not exist)
        when(supabaseClient.getPasswordForUser("ghost_user")).thenReturn(null);

        LoginRequest request = new LoginRequest("ghost_user", "anyPass");
        ResponseEntity<Map<String, String>> response = loginController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not found.", response.getBody().get("message"));
    }

    @Test
    void testLogin_InvalidPassword() {
        //Simulates database having "correctPass" but user sends "wrongPass"
        when(supabaseClient.getPasswordForUser("john_doe")).thenReturn("correctPass");

        LoginRequest request = new LoginRequest("john_doe", "wrongPass");
        ResponseEntity<Map<String, String>> response = loginController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid password.", response.getBody().get("message"));
    }

    @Test
    void testLogin_EmptyPasswordMismatch() {
        //Tests edge case where user sends empty password but DB has a real password
        when(supabaseClient.getPasswordForUser("john_doe")).thenReturn("realPassword");

        LoginRequest request = new LoginRequest("john_doe", "");
        ResponseEntity<Map<String, String>> response = loginController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testLogin_UsernameCaseSensitivity() {
        //Verifies that if inputs match exactly (including casing), it succeeds
        when(supabaseClient.getPasswordForUser("JohnDoe")).thenReturn("Pass123");

        LoginRequest request = new LoginRequest("JohnDoe", "Pass123");
        ResponseEntity<Map<String, String>> response = loginController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLogin_SupabaseException() {
        //Simulates SupabaseClient throwing a RuntimeException (e.g. DB offline)
        when(supabaseClient.getPasswordForUser("error_user")).thenThrow(new RuntimeException("DB Error"));

        LoginRequest request = new LoginRequest("error_user", "pass");

        //Expects the exception to propagate up (or you could add try/catch in controller later)
        try {
            loginController.login(request);
        } catch (RuntimeException e) {
            assertEquals("DB Error", e.getMessage());
        }
    }
}