package org.example.firstlabis.config.security.jaas;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Principal;

/**
 * Principal для представления привилегии.
 */
@Getter
@AllArgsConstructor
public class PrivilegePrincipal implements Principal {
    
    private final String privilege;
    
    @Override
    public String getName() {
        return privilege;
    }
} 