
package bw.co.varsitylend.backend.admin;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthenticationController {

    private final AdminAuthenticationService authenticationService;

    private final HttpSessionSecurityContextRepository securityContextRepository;

    public AdminAuthenticationController(
            AdminAuthenticationService authenticationService,
            HttpSessionSecurityContextRepository securityContextRepository) {

        this.authenticationService = authenticationService;
        this.securityContextRepository = securityContextRepository;
    }

    // ADMIN LOGIN

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody AdminLoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        try {
            Admin admin = authenticationService.authenticate(
                loginRequest.email(),
                loginRequest.password()
            );

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    admin.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

            SecurityContext context =
                SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // Create a new session and prevent session fixation.
            request.getSession(true);
            request.changeSessionId();

            // Save authentication in the HTTP session.
            securityContextRepository.saveContext(
                context,
                request,
                response
            );

            return ResponseEntity.ok(
                Map.of(
                    "message", "Administrator login successful.",
                    "admin", Map.of(
                        "id", admin.getId(),
                        "fullName", admin.getFullName(),
                        "email", admin.getEmail()
                    )
                )
            );

        } catch (BadCredentialsException exception) {

            SecurityContextHolder.clearContext();

            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    Map.of(
                        "message",
                        "Invalid administrator email or password."
                    )
                );
        }
    }

    // VERIFY ADMIN SESSION

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> checkSession() {

        var authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getAuthorities().stream()
                    .noneMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"))) {

            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("authenticated", false));
        }

        return ResponseEntity.ok(
            Map.of(
                "authenticated", true,
                "role", "ADMIN",
                "email", authentication.getName()
            )
        );
    }

    // ADMIN LOGOUT

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpServletRequest request) {

        // Clear Spring Security authentication.
        SecurityContextHolder.clearContext();

        // Invalidate the current administrator session.
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(
            Map.of(
                "message",
                "Administrator logged out successfully."
            )
        );
    }

    public record AdminLoginRequest(
        String email,
        String password
    ) {
    }
}

