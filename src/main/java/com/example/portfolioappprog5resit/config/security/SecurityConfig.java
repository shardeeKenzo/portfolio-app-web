package com.example.portfolioappprog5resit.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); } // BCrypt per slides :contentReference[oaicite:12]{index=12}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF ignore matcher WITHOUT deprecated classes (checks method + path)
        RequestMatcher postInvestors = request ->
                "POST".equals(request.getMethod()) &&
                        "/api/investors".equals(request.getServletPath());

        http
                // Make Security honor our CORS config (bean below).
                .cors(cors -> { })

                // Keep CSRF ON globally; ignore ONLY the client-test endpoint
                // (Assignment requirement).
                .csrf(csrf -> csrf.ignoringRequestMatchers(postInvestors))

                .authorizeHttpRequests(auth -> auth
                        // Public static + public GET pages
                        .requestMatchers(HttpMethod.GET, "/favicon.ico", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/investors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/stocks").permitAll()
                        .requestMatchers(HttpMethod.GET, "/stocks/*").permitAll()

                        // MVC protected screens / writes
                        .requestMatchers("/stocks/addstock").authenticated()
                        .requestMatchers(HttpMethod.POST, "/stocks/addstock").authenticated()
                        .requestMatchers("/investors/**", "/accounts/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/stocks/**", "/investors/**", "/accounts/**").authenticated()

                        // REST reads are public
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll() // CORS preflight

                        // Assignment: allow unauthenticated creation via separate Client app.
                        // 'permitAll' is used here ONLY to test the Client project.
                        .requestMatchers(HttpMethod.POST, "/api/investors").permitAll()

                        // Other API writes remain protected
                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()

                        // Auth pages
                        .requestMatchers("/login", "/register").permitAll()

                        // Safety net
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll())

                // For REST: return 403 instead of redirecting to /login
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    if (req.getRequestURI().startsWith("/api")) {
                        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    } else {
                        res.sendRedirect(req.getContextPath() + "/login");
                    }
                }));

        return http.build();
    }

    /**
     * CORS: allow only your webpack dev origin to call /api/**
     * (Server decides; browser enforces.) Slides: CORS section.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:9000")); // webpack dev origin
        cfg.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Accept", "Content-Type"));
        cfg.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/api/**", cfg);
        return src;
    }
}

