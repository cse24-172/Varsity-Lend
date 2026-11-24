
package bw.co.varsitylend.backend;

import java.sql.Connection;
import javax.sql.DataSource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to Varsity Lend Backend!";
    }

    @GetMapping("/api/health")
    public String health() {
        return "Varsity Lend backend is running!";
    }

    @GetMapping("/api/database-check")
    public String databaseCheck() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5)
                    ? "Neon PostgreSQL connected successfully!"
                    : "Database connection is not valid.";
        } catch (Exception e) {
            return "Database connection failed: " + e.getClass().getSimpleName();
        }
    }
}
