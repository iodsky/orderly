package com.iodsky.orderly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

/*
    🔒 Default Spring Security behavior:
    -----------------------------------
    - When Spring Security is on the classpath, it enables CSRF protection by default.
    - CSRF applies to "unsafe" HTTP methods:
        • POST
        • PUT
        • PATCH
        • DELETE
      (because these methods modify state on the server).
    - This is why, out of the box, only GET requests work without extra configuration.

    ⚡ Why disable CSRF for REST APIs?
    ---------------------------------
    - CSRF is mainly a risk for web application that rely on session cookies.
    - In REST APIs, we usually use tokens (e.g., JWT) or HTTP headers for authentication.
    - Because of that, CSRF protection is unnecessary and gets in the way of POST/PUT/PATCH/DELETE.
    - So we disable CSRF explicitly.

    🛠️ What this config does:
    -------------------------
    - Disables CSRF
    - Requires authentication for every request (`anyRequest().authenticated()`)
    - Uses HTTP Basic authentication (username/password sent via Authorization header)
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable Cross-Site Request Forgery
                .authorizeHttpRequests(auth -> auth
                        // every request must be authenticated
                        .anyRequest().authenticated()
                ).httpBasic();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
