package com.example.backend_sis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())           // Desactivar CSRF para APIs
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()           // TODO: por ahora permitimos todo
                )
                .formLogin(form -> form.disable())       // Desactiva login de Spring Security
                .httpBasic(httpBasic -> httpBasic.disable()); // Desactiva Basic Auth

        return http.build();
    }
}
