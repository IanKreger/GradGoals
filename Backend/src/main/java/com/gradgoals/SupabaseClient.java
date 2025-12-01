package com.gradgoals;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;

@Component
public class SupabaseClient {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Test method to call Supabase
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
    
    public String getPasswordForUser(String username) {
        // 1. Build the URL. 
        // We filter where username equals the input, and we select only the password column.
        String endpoint = supabaseUrl + "/rest/v1/users?username=eq." + username + "&select=password";

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. Execute the request.
        // We expect a JSON list back (e.g., [{"password": "123"}]).
        // We use ParameterizedTypeReference to automatically turn that JSON into a Java List.
        ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(
            endpoint, 
            HttpMethod.GET, 
            entity, 
            new ParameterizedTypeReference<List<Map<String, String>>>() {}
        );

        List<Map<String, String>> result = response.getBody();

        // 3. Extract the password.
        // If the list is not empty, we found the user.
        if (result != null && !result.isEmpty()) {
            return result.get(0).get("password");
        }
        
        // Return null if user was not found
        return null; 
    }
}
