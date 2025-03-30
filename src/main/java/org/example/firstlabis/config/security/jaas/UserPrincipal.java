package org.example.firstlabis.config.security.jaas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.firstlabis.model.security.User;

import java.security.Principal;

/**
 * Principal для представления пользователя.
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