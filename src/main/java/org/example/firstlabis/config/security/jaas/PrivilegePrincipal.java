package org.example.firstlabis.config.security.jaas;

import java.security.Principal;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Principal для представления привилегии.
 * 
 * @author amphyxs
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