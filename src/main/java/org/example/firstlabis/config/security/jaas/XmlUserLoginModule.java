package org.example.firstlabis.config.security.jaas;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.example.firstlabis.model.security.User;
import org.example.firstlabis.service.security.xml.XmlUserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

/**
 * Реализация JAAS LoginModule для аутентификации юзеров через XML.
 * 
 * XmlUserLoginModule - основной модуль аутентификации JAAS, который:
 * - Проверяет учетные данные пользователя
 * - Создает UserPrincipal, RolePrincipal и PrivilegePrincipal
 * - Добавляет их в Subject после успешной аутентификации
 * 
 * @author amphyxs
 */
@Slf4j
public class XmlUserLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private boolean succeeded = false;
    private boolean commitSucceeded = false;
    private String username;
    private UserPrincipal userPrincipal;
    
    private XmlUserService xmlUserService;
    private PasswordEncoder passwordEncoder;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        
        this.xmlUserService = JaasSpringContext.getBean(XmlUserService.class);
        this.passwordEncoder = JaasSpringContext.getBean(PasswordEncoder.class);
    }

    @Override
    public boolean login() throws LoginException {
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("username");
        callbacks[1] = new PasswordCallback("password", false);

        try {
            callbackHandler.handle(callbacks);
            username = ((NameCallback) callbacks[0]).getName();
            String password = new String(((PasswordCallback) callbacks[1]).getPassword());

            // Authenticate user
            Optional<User> userOpt = xmlUserService.findByUsername(username);
            if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
                User user = userOpt.get();
                userPrincipal = new UserPrincipal(user);
                succeeded = true;
                return true;
            } else {
                throw new BadCredentialsException("Invalid username or password");
            }
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public boolean commit() throws LoginException {
        if (!succeeded) {
            return false;
        }

        if (userPrincipal != null) {
            subject.getPrincipals().add(userPrincipal);
            
            // Add role principal
            subject.getPrincipals().add(new RolePrincipal(userPrincipal.getUser().getRole().name()));
            
            // Add privilege principals
            userPrincipal.getUser().getRole().getPrivileges().forEach(privilege -> 
                subject.getPrincipals().add(new PrivilegePrincipal(privilege.name()))
            );
        }

        commitSucceeded = true;
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        if (!succeeded) {
            return false;
        }

        if (succeeded && !commitSucceeded) {
            succeeded = false;
            username = null;
            userPrincipal = null;
        } else {
            logout();
        }
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().remove(userPrincipal);
        succeeded = false;
        commitSucceeded = false;
        username = null;
        userPrincipal = null;
        return true;
    }
} 