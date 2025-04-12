package org.example.firstlabis.initializer;

import org.example.firstlabis.model.security.Role;
import org.example.firstlabis.model.security.User;
import org.example.firstlabis.service.security.xml.XmlUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Инициализатор дефолтных пользователей при запуске приложения.
 * 
 * @author amphyxs
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserInitializer {
    
    private final XmlUserService xmlUserService;
    
    @Bean
    public CommandLineRunner initUsers() {
        return args -> {
            // Create admin user if not exists
            if (xmlUserService.findByUsername("admin").isEmpty()) {
                User adminUser = User.builder()
                        .username("admin")
                        .password("admin") // Will be encrypted by the service
                        .role(Role.ADMIN)
                        .enabledStatus(true)
                        .build();
                
                xmlUserService.saveUser(adminUser);
                log.info("Created default admin user");
            }
            
            if (xmlUserService.findByUsername("user").isEmpty()) {
                User regularUser = User.builder()
                        .username("user")
                        .password("user") // Will be encrypted by the service
                        .role(Role.USER)
                        .enabledStatus(true)
                        .build();
                
                xmlUserService.saveUser(regularUser);
                log.info("Created default user");
            }
        };
    }
} 