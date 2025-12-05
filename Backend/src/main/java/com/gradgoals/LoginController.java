package com.gradgoals;



// Imports for Spring Boot web handling (controllers, REST annotations, HTTP responses)
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

// Imports for storing small response maps we send back to the frontend
import java.util.HashMap;
import java.util.Map;

// BCrypt library used to verify hashed passwords
import org.mindrot.jbcrypt.BCrypt;

// @RestController: Marks this class as a Spring MVC controller where every method returns a domain object instead of a view.
// @CrossOrigin: Configures CORS (Cross-Origin Resource Sharing) to allow requests specifically from your deployed frontend.
@RestController
@CrossOrigin(origins = "https://gradgoals1.onrender.com")
public class LoginController {

    // Dependency: The client used to interact with your Supabase database
    private final SupabaseClient supabaseClient;

    // Constructor Injection: Spring Boot automatically injects the configured SupabaseClient here.
    public LoginController(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    /**
     * Endpoint: POST /login
     * Purpose: Receives login credentials, validates them against the database, and returns success/failure.
     * * @param loginRequest The request body containing the username and password sent by the frontend.
     * @return A ResponseEntity containing a message map and the appropriate HTTP status code.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {

        // Create a map to hold the JSON response data
        Map<String, String> response = new HashMap<>();

        // Extract the raw username and password provided by the user
        String inputUsername = loginRequest.getUsername();
        String inputPassword = loginRequest.getPassword();

        // 1. Database Lookup: Retrieve the stored password hash for the given username from Supabase.
        String storedPasswordHash = supabaseClient.getPasswordForUser(inputUsername);

        // 2. User Existence Check: If the database returns null, the username doesn't exist.
        if (storedPasswordHash == null) {
            response.put("message", "User not found.");
            // Return 401 Unauthorized to indicate authentication failed
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // 3. Password Verification: Use BCrypt to compare the raw input password against the stored hash.
        // checkpw() handles the salting and hashing logic securely.
        if (BCrypt.checkpw(inputPassword, storedPasswordHash)) {
            // Success: Password matches
            response.put("message", "Login successful!");
            response.put("username", inputUsername); // Send username back so frontend can store it in localStorage
            
            // Return 200 OK to indicate success
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // Failure: Password does not match
            response.put("message", "Invalid password.");
            // Return 401 Unauthorized
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}