package com.gradgoals;

// Imports tools from Spring and Java needed for handling API requests,
// sending responses, and building small data objects.
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;


@RestController // Tells Spring this class handles HTTP requests (API endpoints)
@CrossOrigin(origins = "https://gradgoals1.onrender.com") 
// Allows your frontend (Render URL) to call this backend
public class CreateAccountController {

    // A custom class that talks to Supabase (the database)
    private final SupabaseClient supabaseClient;

    // Spring automatically gives this controller a SupabaseClient instance
    public CreateAccountController(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    // This endpoint runs when the frontend sends a POST request to /create-account
    @PostMapping("/create-account")
    public ResponseEntity<Map<String, String>> createAccount(@RequestBody LoginRequest request) {

        // This map will hold the response message we send back to the frontend
        Map<String, String> response = new HashMap<>();

        // Extract username and password from the JSON request
        String newUsername = request.getUsername();
        String newPassword = request.getPassword();

        // 1. Basic Validation
        // Makes sure the user didn't leave fields blank
        if (newUsername == null || newUsername.trim().isEmpty() ||
            newPassword == null || newPassword.trim().isEmpty()) {

            response.put("message", "Username and password cannot be empty.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 error
        }

        // 2. Check if user already exists
        // If Supabase returns a password, that means this username already exists in the database
        String existingPassword = supabaseClient.getPasswordForUser(newUsername);
        if (existingPassword != null) {
            response.put("message", "User already exists. Please choose a different username.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 error
        }

        // 3. Create the user in Supabase
        boolean success = supabaseClient.createUser(newUsername, newPassword);

        // If the user was inserted successfully
        if (success) {
            response.put("message", "Account created successfully!");
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
        } 
        // If something failed inside Supabase
        else {
            response.put("message", "Failed to create account. Database error.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 error
        }
    }
}
