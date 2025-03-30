package org.example.firstlabis.service.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.firstlabis.dto.authentication.request.UserDto;
import org.example.firstlabis.dto.authentication.response.JwtResponseDTO;
import org.example.firstlabis.dto.authentication.request.LoginRequestDTO;
import org.example.firstlabis.dto.authentication.request.RegisterRequestDTO;
import org.example.firstlabis.model.security.Role;
import org.example.firstlabis.model.security.User;
import org.example.firstlabis.service.security.xml.XmlUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final XmlUserService xmlUserService;

    @Value("${application.security.unique-password-constraint:false}")
    private boolean uniquePasswordConstraint; // настройка уникальности паролей

    public JwtResponseDTO authenticate(LoginRequestDTO request) {
        log.info("Authenticating user: {}", request.username());
        User user = xmlUserService.findByUsername(request.username())
                .orElseThrow(() -> {
                    log.error("User not found during authentication: {}", request.username());
                    return new UsernameNotFoundException("User not found with username: " + request.username());
                });

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password(),
                            user.getAuthorities()));
            log.info("User authenticated successfully: {}", request.username());
            log.info("User authorities: {}", user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            return generateJwt(user);
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw e;
        }
    }

    public JwtResponseDTO registerUser(RegisterRequestDTO request) {
        log.info("Registering new user: {}", request.username());
        return registerEnabled(request, Role.USER, true);
    }

    /**
     * Метод регистрации и активации пользователя(для role:admin в первых раз без
     * активации, пока ее не подтвердят)
     */
    private JwtResponseDTO registerEnabled(RegisterRequestDTO request, Role role, Boolean enabled) {
        User user = createUser(request, role, enabled);
        return generateJwt(user);
    }

    /**
     * Принять запрос на регистрацию пользователя в системе
     */
    public JwtResponseDTO submitAdminRegistrationRequest(RegisterRequestDTO request) {
        // In the XML implementation we don't worry about checking for first admin
        // as we initialize with a default admin user
        return registerEnabled(request, Role.ADMIN, false);
    }

    /**
     * Create user with specific role and activation status
     */
    private User createUser(RegisterRequestDTO request, Role role, boolean enabled) {
        validateRegisterRequest(request);
        User user = mapToUser(request, role, enabled);
        xmlUserService.saveUser(user);
        log.info("User created successfully: {}, role: {}", request.username(), role);
        return user;
    }

    /**
     * Метод валидации запроса на регистрацию
     */
    private void validateRegisterRequest(RegisterRequestDTO request) {
        validateUsername(request.username());
        // Skip password uniqueness validation for XML implementation
    }

    /**
     * Метод валидации имени пользователя
     */
    private void validateUsername(String username) {
        if (xmlUserService.findByUsername(username).isPresent()) {
            log.warn("Username already taken: {}", username);
            throw new AuthenticationServiceException("Username " + username + " is taken");
        }
    }

    /**
     * Generate JWT token
     */
    private JwtResponseDTO generateJwt(User user) {
        String jwt = jwtService.generateToken(user);
        log.info("Generated JWT token for user: {}", user.getUsername());
        return new JwtResponseDTO(jwt,
                user.getAuthorities().stream().filter(authority -> authority.getAuthority().contains("ROLE"))
                        .findFirst()
                        .map(GrantedAuthority::getAuthority).orElse("ROLE_USER"));
    }

    private User mapToUser(RegisterRequestDTO request, Role role, Boolean enabled) {
        return User.builder()
                .username(request.username())
                .password(request.password()) // Will be encoded by XmlUserService when saved
                .role(role)
                .enabledStatus(enabled)
                .build();
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    public Page<UserDto> getPendingRegistrationRequest(Pageable pageable) {
        // In XML implementation, simplify this to return empty page
        return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    /**
     * Метод активации пользователя, который отправлял запроса на статус админа
     */
    public void approveAdminRegistrationRequest(String username) {
        User user = xmlUserService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        user.setEnabledStatus(true); // активируем пользователя
        xmlUserService.saveUser(user);
    }

    /**
     * Метод отказа пользователю, который запросил админ права
     */
    public void rejectAdminRegistrationRequest(String username) {
        User user = xmlUserService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        validateUserNotEnabled(user); // проверяем не был ли ранее активирован этот пользователь
        xmlUserService.deleteUserByUsername(username);
    }

    /**
     * Метод проверяет не активирован ли пользователь
     * Если активирован, то выкинет исключение
     * Необходим для проверок при попытках удалить действующих администраторов
     */
    private void validateUserNotEnabled(User user) {
        if (user.isEnabledStatus()) {
            throw new AuthenticationServiceException("Cannot delete an enabled user");
        }
    }

    /**
     * Метод, проверяющий есть в хранилище хотя бы один админ
     */
    public boolean hasRegisteredAdmin() {
        return xmlUserService.getAllUsers().stream().anyMatch(u -> u.getRole() == Role.ADMIN);
    }
}