
package bw.co.varsitylend.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "Welcome to Varsity Lend Backend!";
    }

    @GetMapping("/api/health")
    public String health() {
        return "Varsity Lend backend is running!";
    }
}

