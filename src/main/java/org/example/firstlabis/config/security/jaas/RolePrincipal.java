package org.example.firstlabis.config.security.jaas;

import java.security.Principal;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Principal для роли.
 * 
 * @author amphyxs
 */
@Getter
@AllArgsConstructor
public class RolePrincipal implements Principal {
    
    private final String role;
    
    @Override
    public String getName() {
        return role;
    }
} 