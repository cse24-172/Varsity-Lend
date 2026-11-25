
package bw.co.varsitylend.backend.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    name = "app.admin.bootstrap.enabled",
    havingValue = "true"
)
public class AdminDataInitializer implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_BOOTSTRAP_NAME:}")
    private String adminName;

    @Value("${ADMIN_BOOTSTRAP_EMAIL:}")
    private String adminEmail;

    @Value("${ADMIN_BOOTSTRAP_PASSWORD:}")
    private String adminPassword;

    public AdminDataInitializer(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (adminName.isBlank()
                || adminEmail.isBlank()
                || adminPassword.isBlank()) {

            throw new IllegalStateException(
                "Admin bootstrap is enabled, but administrator environment variables are missing."
            );
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();

        if (adminRepository.existsByEmail(normalizedEmail)) {
            System.out.println(
                "Administrator account already exists. No account created."
            );
            return;
        }

        Admin admin = new Admin();

        admin.setFullName(adminName.trim());
        admin.setEmail(normalizedEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setActive(true);

        adminRepository.save(admin);

        System.out.println(
            "Initial administrator account created successfully."
        );
    }
}
