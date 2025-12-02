package com.gradgoals;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.HashMap; // Required for creating the new user object

@Component
public class SupabaseClient {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // ---------------------------------------------------------
    // 1. TEST METHOD
    // ---------------------------------------------------------
    public String getTestData() {
        String endpoint = supabaseUrl + "/rest/v1/users";

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
    
    // ---------------------------------------------------------
    // 2. LOGIN METHOD (GET PASSWORD)
    // ---------------------------------------------------------
    public String getPasswordForUser(String username) {
        // Build the URL to filter by username and select only the password
        String endpoint = supabaseUrl + "/rest/v1/users?username=eq." + username + "&select=password";

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Execute the request expecting a List of Maps
        ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(
            endpoint, 
            HttpMethod.GET, 
            entity, 
            new ParameterizedTypeReference<List<Map<String, String>>>() {}
        );

        List<Map<String, String>> result = response.getBody();

        // If list is not empty, return the password from the first result
        if (result != null && !result.isEmpty()) {
            return result.get(0).get("password");
        }
        
        // Return null if user was not found
        return null; 
    }

    // ---------------------------------------------------------
    // 3. CREATE ACCOUNT METHOD (NEW)
    // ---------------------------------------------------------
    public boolean createUser(String username, String password) {
        String endpoint = supabaseUrl + "/rest/v1/users";

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("Content-Type", "application/json");
        // This header ensures Supabase returns the created row, confirming success
        headers.set("Prefer", "return=representation"); 

        // Create a Map to hold the data (JSON object)
        Map<String, String> newUser = new HashMap<>();
        newUser.put("username", username);
        newUser.put("password", password);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(newUser, headers);

        try {
            // Send POST request to INSERT data
            ResponseEntity<String> response = restTemplate.exchange(
                endpoint, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            
            // If we get a 201 Created status, the account was made successfully
            return response.getStatusCode() == HttpStatus.CREATED;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}