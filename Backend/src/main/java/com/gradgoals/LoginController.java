package com.gradgoals;

// This controller handles login by checking a user's credentials against Supabase.

// Imports for Spring Boot web handling (controllers, REST annotations, HTTP responses)
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

// Imports for storing small response maps we send back to the frontend
import java.util.HashMap;
import java.util.Map;

// BCrypt library used to verify hashed passwords
import org.mindrot.jbcrypt.BCrypt;

@RestController
@CrossOrigin(origins = "https://gradgoals1.onrender.com")
public class LoginController {

    // Supabase client used to fetch user records
    private final SupabaseClient supabaseClient;

    // Spring injects the Supabase client into this controller
    public LoginController(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    // POST /login — validates the user's username & password
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {

        Map<String, String> response = new HashMap<>();

        String inputUsername = loginRequest.getUsername();
        String inputPassword = loginRequest.getPassword();

        // 1. Get stored hashed password from Supabase
        String storedPasswordHash = supabaseClient.getPasswordForUser(inputUsername);

        // 2. If username does not exist → fail login
        if (storedPasswordHash == null) {
            response.put("message", "User not found.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // 3. Check entered password against hashed password
        if (BCrypt.checkpw(inputPassword, storedPasswordHash)) {
            response.put("message", "Login successful!");
            response.put("username", inputUsername);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Invalid password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
