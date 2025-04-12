package org.example.firstlabis.config.security;

import org.example.firstlabis.filter.JwtAuthenticationFilter;
import org.example.firstlabis.model.security.Privilege;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigFilterChain {
    private static final String[] WHITE_LIST_URL = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/notify/**",
            "/app/**",
            "/test/**",
            "/app.js"
    };

    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfigFilterChain(@Lazy AuthenticationManager authenticationManager,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(WHITE_LIST_URL).permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/videos/for-review").hasAnyAuthority(
                                Privilege.REVIEW_VIDEO.name())

                        .requestMatchers(HttpMethod.POST, "/api/videos/*/moderate").hasAnyAuthority(
                                Privilege.REVIEW_VIDEO.name())

                        .requestMatchers(HttpMethod.POST, "/api/videos").hasAnyAuthority(
                                Privilege.CREATE_VIDEO.name())

                        .requestMatchers(HttpMethod.GET, "/api/videos/*").hasAnyAuthority(
                                Privilege.VIEW_VIDEO.name())

                        .requestMatchers(HttpMethod.GET, "/api/videos/approved-rejected").hasAnyAuthority(
                                Privilege.VIEW_VIDEO.name())

                        .requestMatchers(HttpMethod.POST, "/api/videos/complaints").hasAnyAuthority(
                                Privilege.CREATE_COMPLAINT.name())

                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
