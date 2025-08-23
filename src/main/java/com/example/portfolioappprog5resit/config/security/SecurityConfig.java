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
                // ✅ CSRF ON (default). Remove the old `.csrf(csrf -> csrf.disable())`
                .csrf(csrf -> {})

                .authorizeHttpRequests(auth -> auth
                        // static + public … (keep your existing matchers)
                        .requestMatchers("/favicon.ico", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/investors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/stocks").permitAll()

                        // protected screens
                        .requestMatchers("/stocks/addstock").authenticated()
                        .requestMatchers(HttpMethod.POST, "/stocks/addstock").authenticated()
                        .requestMatchers(HttpMethod.GET, "/stocks/*").permitAll()

                        .requestMatchers("/investors/**", "/accounts/**").authenticated()

                        // safety net for MVC writes
                        .requestMatchers(HttpMethod.POST, "/stocks/**", "/investors/**", "/accounts/**").authenticated()

                        // REST API contract remains: reads free, writes require auth
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()

                        .requestMatchers("/login", "/register").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll())
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    if (req.getRequestURI().startsWith("/api")) res.setStatus(403);
                    else res.sendRedirect(req.getContextPath() + "/login");
                }));

        return http.build();
    }
}
