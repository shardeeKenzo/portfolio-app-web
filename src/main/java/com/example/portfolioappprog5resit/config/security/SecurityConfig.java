package com.example.portfolioappprog5resit.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); } // BCrypt per slides :contentReference[oaicite:12]{index=12}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // TEMP: keep CSRF off so your REST & AJAX examples work unchanged
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // ---------- Static ----------
                        .requestMatchers("/favicon.ico", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()

                        // ---------- Public (Guest) ----------
                        .requestMatchers(HttpMethod.GET, "/").permitAll()           // welcome.html
                        .requestMatchers(HttpMethod.GET, "/investors").permitAll()  // investors.html (list)
                        .requestMatchers(HttpMethod.GET, "/stocks").permitAll()     // stocks.html (list)

                        // ---------- PROTECTED must come BEFORE the broad /stocks/* ----------
                        .requestMatchers("/stocks/addstock").authenticated()                 // addstock.html (GET)
                        .requestMatchers(HttpMethod.POST, "/stocks/addstock").authenticated()// form submit (explicit; also covered by writes rule)

                        // Now safe to allow public stock details
                        .requestMatchers(HttpMethod.GET, "/stocks/*").permitAll()            // stockdetails.html (by id)

                        // Other logged-only pages
                        .requestMatchers("/investors/addinvestor").authenticated()           // addinvestor.html (GET/POST)
                        .requestMatchers("/investors/*/addaccount").authenticated()          // addaccount.html (GET/POST)
                        .requestMatchers(HttpMethod.GET, "/investors/*").authenticated()     // investordetails.html
                        .requestMatchers("/accounts/**").authenticated()                     // accountdetails.html & actions

                        // MVC writes (safety net)
                        .requestMatchers(HttpMethod.POST, "/stocks/**", "/investors/**", "/accounts/**").authenticated()

                        // REST API
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()

                        // Auth pages
                        .requestMatchers("/login", "/register").permitAll()

                        // Everything else
                        .anyRequest().authenticated()
                )

                // Custom login page + simple logout
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

                // For REST: if unauthenticated hits /api/*, return 403 instead of HTML redirect
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    if (req.getRequestURI().startsWith("/api/")) {
                        res.setStatus(403);
                    } else {
                        res.sendRedirect(req.getContextPath() + "/login");
                    }
                }));

        return http.build();
    }
}
