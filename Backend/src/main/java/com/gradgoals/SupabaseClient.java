package com.gradgoals;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;
import org.mindrot.jbcrypt.BCrypt;  

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * SupabaseClient
 * * This class acts as a service to communicate with the Supabase backend via HTTP REST API.
 * It is marked with @Component so Spring Boot handles its lifecycle and injection.
 * * Main Responsibilities:
 * 1. Fetching user password hashes for login verification.
 * 2. Creating new users and securely hashing their passwords before storage.
 */
@Component
public class SupabaseClient {

    // Reads the "supabase.url" value from your application.properties file
    @Value("${supabase.url}")
    private String supabaseUrl;

    // Reads the "supabase.key" (anon key) from your application.properties file
    @Value("${supabase.key}")
    private String supabaseKey;

    // RestTemplate is a Spring utility for making HTTP requests (GET, POST, etc.)
    private final RestTemplate restTemplate = new RestTemplate();


    // ---------------------------------------------------------
    // 1. LOGIN METHOD (GET PASSWORD HASH)
    // ---------------------------------------------------------
    /**
     * Retrieves the stored password hash for a specific username.
     * Used during Login to verify if the entered password matches the stored hash.
     * Returns the hashed password if found, or null if the user does not exist.
     */
    public String getPasswordForUser(String username) {
        // Construct the URL with a filter query parameter: username=eq.VALUE
        // This tells Supabase to only return rows where the username column equals the input.
        String endpoint = supabaseUrl + "/rest/v1/users?username=eq." + username + "&select=password";

        // Set up the required headers for Supabase Authentication
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Execute the GET request. 
        // We expect a List of Maps because Supabase always returns an array of JSON objects.
        ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, String>>>() {}
        );

        List<Map<String, String>> result = response.getBody();

        // If we got a result, extract the password field from the first item
        if (result != null && !result.isEmpty()) {
            return result.get(0).get("password");  
        }

        // Return null if user was not found
        return null;
    }

    // ---------------------------------------------------------
    // 2. CREATE ACCOUNT METHOD (HASHES PASSWORD)
    // ---------------------------------------------------------
    /**
     * Creates a new user row in the Supabase 'users' table.
     * This method uses BCrypt to hash the password before sending it, ensuring it never stores plain-text passwords.
     * returns true if creation was successful, false otherwise.
     */
    public boolean createUser(String username, String password) {
        String endpoint = supabaseUrl + "/rest/v1/users";

        // Set headers. Content-Type is required for POST requests sending JSON data.
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("Content-Type", "application/json");
        // "Prefer: return=representation" asks Supabase to return the created object in the response
        headers.set("Prefer", "return=representation");

        // SECURITY: Hash the password using BCrypt with a generated salt.
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Create the payload map to convert to JSON
        Map<String, String> newUser = new HashMap<>();
        newUser.put("username", username);
        newUser.put("password", hashedPassword);  

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(newUser, headers);

        try {
            // Execute the POST request to insert the new user
            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return response.getStatusCode() == HttpStatus.CREATED;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}