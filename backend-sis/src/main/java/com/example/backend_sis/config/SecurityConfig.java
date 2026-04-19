package com.example.backend_sis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/*/password").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/*/reset-password").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/medicos").hasAnyRole("ADMINISTRATIVO", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/me/password").authenticated()

                        .requestMatchers("/api/historias-clinicas/**").hasRole("MEDICO")

                        .requestMatchers(HttpMethod.POST, "/api/agendas/**").hasAnyRole("ADMINISTRATIVO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/agendas/**").hasAnyRole("ADMINISTRATIVO", "MEDICO", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/turnos/dia").hasAnyRole("ADMINISTRATIVO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/turnos/medico/**").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.PATCH, "/api/turnos/*/asignar-paciente").hasAnyRole("ADMINISTRATIVO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/turnos/*/estado").hasAnyRole("ADMINISTRATIVO", "MEDICO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/turnos/*/reprogramar").hasAnyRole("ADMINISTRATIVO", "ADMIN")

                        .requestMatchers("/api/pacientes/**").hasAnyRole("ADMINISTRATIVO", "ADMIN", "MEDICO")
                        .requestMatchers("/api/episodios/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/camas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/camas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/camas/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/camas/disponibles/hospitalizacion").hasAnyRole("ADMINISTRATIVO", "MEDICO", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/reconocimiento/rostro/**").hasAnyRole("ADMINISTRATIVO", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reconocimiento/rostros/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reconocimiento/rostros/*").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/huellas/registrar").hasAnyRole("ADMINISTRATIVO", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/huellas/buscar").hasAnyRole("ADMINISTRATIVO", "ADMIN", "MEDICO")
                        .requestMatchers(HttpMethod.GET, "/api/huellas/paciente/*").hasAnyRole("ADMINISTRATIVO", "ADMIN", "MEDICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/huellas/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/huellas/admin").hasRole("ADMIN")


                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
