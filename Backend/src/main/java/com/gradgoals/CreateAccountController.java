package com.gradgoals;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin(origins = "*") // Allow Netlify to talk to this. This is too fix something minor
public class CreateAccountController {

    private final SupabaseClient supabaseClient;

    public CreateAccountController(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    @PostMapping("/create-account")
    public ResponseEntity<Map<String, String>> createAccount(@RequestBody LoginRequest request) {
        Map<String, String> response = new HashMap<>();

        String newUsername = request.getUsername();
        String newPassword = request.getPassword();

        // 1. Basic Validation
        if (newUsername == null || newUsername.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            response.put("message", "Username and password cannot be empty.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // 2. Check if user already exists
        // We reuse the existing method. If it returns a password, the user exists.
        String existingPassword = supabaseClient.getPasswordForUser(newUsername);
        if (existingPassword != null) {
            response.put("message", "User already exists. Please choose a different username.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict
        }

        // 3. Create the User in Supabase
        boolean success = supabaseClient.createUser(newUsername, newPassword);

        if (success) {
            response.put("message", "Account created successfully!");
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
        } else {
            response.put("message", "Failed to create account. Database error.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}