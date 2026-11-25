
package bw.co.varsitylend.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Temporary during API integration.
            // CSRF protection must be restored before production deployment.
            .csrf(csrf -> csrf.disable())

            // Use server-side sessions instead of JWT.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            .authorizeHttpRequests(auth -> auth

                // Allow browser preflight requests.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public authentication endpoints.
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/admin/login").permitAll()

                // Public health and database checks.
                .requestMatchers(
                    HttpMethod.GET,
                    "/",
                    "/api/health",
                    "/api/database-check"
                ).permitAll()

                // Only administrators can access admin endpoints.
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Only authenticated students can access student endpoints.
                .requestMatchers("/api/student/**").hasRole("STUDENT")

                // Everything else is blocked by default.
                .anyRequest().denyAll()
            );

        return http.build();
    }
}

