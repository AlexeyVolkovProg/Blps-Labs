package org.example.firstlabis.controller.authentication;

import org.example.firstlabis.dto.authentication.request.LoginRequestDTO;
import org.example.firstlabis.dto.authentication.request.RegisterRequestDTO;
import org.example.firstlabis.dto.authentication.response.JwtResponseDTO;
import org.example.firstlabis.service.security.jwt.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered user"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data")
    })
    @PostMapping("/register")
    public ResponseEntity<JwtResponseDTO> register(
            @Parameter(description = "User registration data") @RequestBody RegisterRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerUser(request));
    }

    @Operation(summary = "User login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(
            @Parameter(description = "User login credentials") @RequestBody LoginRequestDTO request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    /**
     * Принимаем запрос на регистрацию нового администратора
     */
    @Operation(summary = "Request registration for a new admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Admin registration request submitted"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data")
    })
    @PostMapping("/register-admin")
    public ResponseEntity<JwtResponseDTO> registerAdmin(@RequestBody RegisterRequestDTO request) {
        if (authenticationService.hasRegisteredAdmin()) { // проверка на наличие админа в системе, который может обработать
            return ResponseEntity.ok(authenticationService.submitAdminRegistrationRequest(request));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
