package com.udea.bancodigital.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  SPRINT 0 — ESCUDO DE SEGURIDAD ABIERTO (TEMPORAL)          ║
 * ║                                                              ║
 * ║  PROPÓSITO: Permite que los Sprints 1 y 2 construyan y       ║
 * ║  prueben sus endpoints sin bloqueo de autenticación.         ║
 * ║                                                              ║
 * ║  ACCIÓN REQUERIDA EN SPRINT 3:                               ║
 * ║  Reemplazar permitAll() con JwtAuthenticationFilter y        ║
 * ║  las reglas RBAC por rol.                                    ║
 * ║                                                              ║
 * ║  ⚠️  NUNCA desplegar en producción con esta configuración.   ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger / OpenAPI
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api-docs/**",
                    "/api-docs.yaml"
                ).permitAll()
                // Actuator (health checks para Docker/Render)
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // ── TEMPORAL Sprint 0-2: todo permitido ──────────────────────
                // TODO Sprint 3: reemplazar con .authenticated() y filtro JWT
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
