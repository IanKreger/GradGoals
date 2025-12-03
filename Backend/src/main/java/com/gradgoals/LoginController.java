package com.gradgoals;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

@RestController
@CrossOrigin(origins = "https://gradgoals1.onrender.com")
public class LoginController {

    private final SupabaseClient supabaseClient;

    public LoginController(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, String> response = new HashMap<>();

        String inputUsername = loginRequest.getUsername();
        String inputPassword = loginRequest.getPassword();

        // 1. Fetch password hash from Supabase
        String storedPasswordHash = supabaseClient.getPasswordForUser(inputUsername);

        // 2. User not found
        if (storedPasswordHash == null) {
            response.put("message", "User not found.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // 3. Compare hashed password
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
