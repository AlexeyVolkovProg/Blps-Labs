package org.example.firstlabis.config.security.jaas;

import java.security.Principal;

import org.example.firstlabis.model.security.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Principal для представления пользователя.
 * 
 * @author amphyxs
 */
@Getter
@AllArgsConstructor
public class UserPrincipal implements Principal {
    
    private final User user;
    
    @Override
    public String getName() {
        return user.getUsername();
    }
} 