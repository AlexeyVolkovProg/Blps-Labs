package org.example.firstlabis.config.security;

import lombok.RequiredArgsConstructor;
import org.example.firstlabis.service.security.xml.XmlUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final XmlUserService xmlUserService;
    private final DefaultJaasAuthenticationProvider jaasAuthenticationProvider;
    
    public SecurityConfig(@Lazy XmlUserService xmlUserService, 
                          @Lazy DefaultJaasAuthenticationProvider jaasAuthenticationProvider) {
        this.xmlUserService = xmlUserService;
        this.jaasAuthenticationProvider = jaasAuthenticationProvider;
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return xmlUserService;
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(
                daoAuthenticationProvider(),
                jaasAuthenticationProvider
        ));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}