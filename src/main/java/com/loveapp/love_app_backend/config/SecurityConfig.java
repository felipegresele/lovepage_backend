package com.loveapp.love_app_backend.config;

import com.loveapp.love_app_backend.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CORS aplicado explicitamente — sem isso o Spring Security
                // ignora o CorsConfig e bloqueia o preflight OPTIONS com 403
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // CSRF desabilitado pois usamos JWT stateless
                .csrf(csrf -> csrf.disable())

                // Sem sessão — cada request é autenticado pelo token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payment/webhook").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pages/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/love-pages/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/payment/pix/status/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // 🔒 Tudo mais exige autenticação
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}