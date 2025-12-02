package com.gradgoals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Component;

// @Component makes this available to be autowired into your LoginController
@Component
public class SupabaseClient {

    private final String supabaseUrl;
    private final String supabaseKey;
    private final HttpClient httpClient;

    public SupabaseClient() {
        // We get these from environment variables so your secrets aren't hardcoded
        this.supabaseUrl = System.getenv("SUPABASE_URL");
        this.supabaseKey = System.getenv("SUPABASE_KEY");
        this.httpClient = HttpClient.newHttpClient();
    }

    // This is the specific method your LoginController is looking for
    public String getPasswordForUser(String email) {
        // TODO: IMPLEMENT REAL SUPABASE LOGIC HERE
        // For now, this is a placeholder to make the build pass.
        
        System.out.println("Checking Supabase for user: " + email);

        // Example logic:
        // 1. Send HTTP GET request to Supabase table (e.g. /rest/v1/users?email=eq.email)
        // 2. Parse the JSON response
        // 3. Return the stored password hash
        
        // Return null if user not found, or the password hash if found
        return null; 
    }
    
    // Helper method to send requests (Optional, but useful for later)
    public String getData(String tableName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/rest/v1/" + tableName))
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching data";
        }
    }
}