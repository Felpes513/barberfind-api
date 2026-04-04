package com.barberfind.api.config;

import com.barberfind.api.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity // ✅ habilita @PreAuthorize nos controllers
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // ===== ERRO =====
                        .requestMatchers("/error").permitAll()

                        // ===== KEEP-ALIVE (cron / monitor externo) =====
                        .requestMatchers(HttpMethod.GET, "/api/ping").permitAll()
                        .requestMatchers(HttpMethod.GET, "/initi").permitAll()

                        // ===== AUTH =====
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/client").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/barber").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register/owner").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // ===== BARBERSHOPS (GET PÚBLICO) =====
                        .requestMatchers(HttpMethod.GET, "/api/barbershops").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/barbershops/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/barbershops/*/services").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/barbershops/*/photos").permitAll()

                        // ===== SERVIÇOS GLOBAIS =====
                        .requestMatchers(HttpMethod.GET, "/api/services").permitAll()

                        // ===== SWAGGER =====
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // ===== QUALQUER OUTRA ROTA =====
                        .anyRequest().authenticated()
                )

                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}