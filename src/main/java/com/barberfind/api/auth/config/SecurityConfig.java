package com.barberfind.api.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API REST: sem CSRF (por enquanto)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // cadastro público
                        .requestMatchers("/api/auth/register/**").permitAll()

                        // swagger público (opcional, mas ajuda muito agora)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // todo o resto exige login (por enquanto, basic)
                        .anyRequest().authenticated()
                )

                // mantém basic para dev (vamos trocar por JWT depois)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}