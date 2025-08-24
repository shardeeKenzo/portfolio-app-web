package com.example.portfolioappprog5resit.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        RequestMatcher postInvestors = request ->
                "POST".equals(request.getMethod()) &&
                        "/api/investors".equals(request.getServletPath());

        http

                .cors(cors -> { })



                .csrf(csrf -> csrf.ignoringRequestMatchers(postInvestors))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.GET, "/favicon.ico", "/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/investors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/stocks").permitAll()
                        .requestMatchers(HttpMethod.GET, "/stocks/*").permitAll()


                        .requestMatchers("/stocks/addstock").authenticated()
                        .requestMatchers(HttpMethod.POST, "/stocks/addstock").authenticated()
                        .requestMatchers("/investors/**", "/accounts/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/stocks/**", "/investors/**", "/accounts/**").authenticated()


                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll() // CORS preflight

                        .requestMatchers(HttpMethod.POST, "/api/investors").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()

                        .requestMatchers("/login", "/register").permitAll()

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form.loginPage("/login").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll())

                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    if (req.getRequestURI().startsWith("/api")) {
                        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    } else {
                        res.sendRedirect(req.getContextPath() + "/login");
                    }
                }));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:9000"));
        cfg.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Accept", "Content-Type"));
        cfg.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/api/**", cfg);
        return src;
    }
}

