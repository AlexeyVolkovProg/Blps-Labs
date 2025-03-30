package org.example.firstlabis.config.security.jaas;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;
import org.springframework.security.authentication.jaas.memory.InMemoryConfiguration;

import javax.security.auth.login.AppConfigurationEntry;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Конфигурация для интеграции JAAS со Spring Security.
 */
@Configuration
public class JaasSecurityConfig {

    /**
     * Creates a JAAS Configuration that defines our XML authentication
     * @return JAAS Configuration
     */
    @Bean
    public javax.security.auth.login.Configuration jaasConfiguration() {
        Map<String, String> options = new HashMap<>();
        options.put("debug", "true");
        
        AppConfigurationEntry appConfigurationEntry = new AppConfigurationEntry(
                XmlUserLoginModule.class.getName(),
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                options
        );
        
        Map<String, AppConfigurationEntry[]> configMap = new HashMap<>();
        configMap.put("SPRINGSECURITY", new AppConfigurationEntry[] {appConfigurationEntry});
        
        return new InMemoryConfiguration(configMap);
    }

    /**
     * Creates a JAAS Authentication Provider to work with Spring Security
     * @return DefaultJaasAuthenticationProvider
     */
    @Bean
    @Lazy
    public DefaultJaasAuthenticationProvider jaasAuthenticationProvider() {
        DefaultJaasAuthenticationProvider provider = new DefaultJaasAuthenticationProvider();
        provider.setConfiguration(jaasConfiguration());
        provider.setAuthorityGranters(new AuthorityGranter[] {new JaasAuthorityGranter()});
        provider.setLoginContextName("SPRINGSECURITY");
        return provider;
    }

    /**
     * Authority granter that converts JAAS principals to Spring Security authorities
     */
    private static class JaasAuthorityGranter implements AuthorityGranter {
        @Override
        public Set<String> grant(Principal principal) {
            Set<String> authorities = new HashSet<>();
            
            if (principal instanceof RolePrincipal) {
                authorities.add("ROLE_" + principal.getName());
            } else if (principal instanceof PrivilegePrincipal) {
                authorities.add(principal.getName());
            }
            
            return authorities;
        }
    }
} 