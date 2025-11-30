import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
// This annotation allows your Netlify frontend to talk to this Render backend.
// For now, "*" allows ANY website to connect. We will restrict this later for security.
@CrossOrigin(origins = "*") 
public class LoginController {

    private final SupabaseClient supabaseClient;

    // We inject the SupabaseClient we modified in Step 2
    public LoginController(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        Map<String, String> response = new HashMap<>();

        String inputUsername = loginRequest.getUsername();
        String inputPassword = loginRequest.getPassword();

        // 1. Get the password from Supabase using your new method
        String storedPassword = supabaseClient.getPasswordForUser(inputUsername);

        // 2. logic to check if user exists
        if (storedPassword == null) {
            response.put("message", "User not found.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // 3. Logic to check if password matches
        if (inputPassword.equals(storedPassword)) {
            response.put("message", "Login successful!");
            response.put("username", inputUsername);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Invalid password.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}