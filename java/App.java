import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

@RestController
class TestController {

    private final SupabaseClient supabaseClient;

    // Spring injects SupabaseClient automatically
    public TestController(SupabaseClient supabaseClient) {
        this.supabaseClient = supabaseClient;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Backend is running!";
    }

    @GetMapping("/test-supabase")
    public String testSupabase() {
        return supabaseClient.getTestData();
    }
}

