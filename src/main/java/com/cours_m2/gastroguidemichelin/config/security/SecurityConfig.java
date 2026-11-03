package com.cours_m2.gastroguidemichelin.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/restaurants/seed").permitAll()
                        .requestMatchers(HttpMethod.GET, "/restaurants", "/restaurants/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/restaurants/*/evaluations").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/evaluations/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/restaurants/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/evaluations", "/restaurants/*/evaluations").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/evaluations/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter())));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter keycloakJwtConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("scope");

        KeycloakRoleConverter keycloakConverter = new KeycloakRoleConverter();
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(keycloakConverter);
        return jwtConverter;
    }
}
