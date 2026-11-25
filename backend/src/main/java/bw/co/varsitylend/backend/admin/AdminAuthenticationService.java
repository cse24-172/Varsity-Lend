
package bw.co.varsitylend.backend.admin;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthenticationService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthenticationService(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin authenticate(String email, String password) {

        if (email == null || password == null ||
                email.isBlank() || password.isBlank()) {
            throw new BadCredentialsException(
                "Invalid administrator email or password."
            );
        }

        Admin admin = adminRepository
            .findByEmail(email.trim().toLowerCase())
            .orElseThrow(() -> new BadCredentialsException(
                "Invalid administrator email or password."
            ));

        if (!admin.isActive() ||
                !passwordEncoder.matches(password, admin.getPasswordHash())) {
            throw new BadCredentialsException(
                "Invalid administrator email or password."
            );
        }

        return admin;
    }
}
