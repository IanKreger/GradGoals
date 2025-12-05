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
    // 2. LOGIN METHOD (GET PASSWORD HASH)
    // ---------------------------------------------------------
    public String getPasswordForUser(String username) {
        String endpoint = supabaseUrl + "/rest/v1/users?username=eq." + username + "&select=password";

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, String>>>() {}
        );

        List<Map<String, String>> result = response.getBody();

        if (result != null && !result.isEmpty()) {
            return result.get(0).get("password");  
        }

        return null;
    }

    // ---------------------------------------------------------
    // 3. CREATE ACCOUNT METHOD (HASHES PASSWORD)
    // ---------------------------------------------------------
    public boolean createUser(String username, String password) {
        String endpoint = supabaseUrl + "/rest/v1/users";

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.set("Content-Type", "application/json");
        headers.set("Prefer", "return=representation");

        //HASH THE PASSWORD HERE
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Map<String, String> newUser = new HashMap<>();
        newUser.put("username", username);
        newUser.put("password", hashedPassword);  

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(newUser, headers);

        try {
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
