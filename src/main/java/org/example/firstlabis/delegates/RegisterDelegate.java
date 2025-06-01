package org.example.firstlabis.delegates;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.authentication.request.RegisterRequestDTO;
import org.example.firstlabis.dto.authentication.response.JwtResponseDTO;
import org.example.firstlabis.service.security.jwt.AuthenticationService;
import org.example.firstlabis.model.security.Role;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Slf4j
@Component("registerDelegate")
@RequiredArgsConstructor
public class RegisterDelegate implements JavaDelegate {
    
    private final AuthenticationService authenticationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("✅ Был вызван  RegisterDelegate, отвечающий за регистрацию пользователя");
        String username = (String) delegateExecution.getVariable("username");
        String password = (String) delegateExecution.getVariable("password");
        Role userType = Role.valueOf((String) delegateExecution.getVariable("userType"));

        var loginRequest = new RegisterRequestDTO(username, password);
        JwtResponseDTO jwtResponseDTO;
        try {
            jwtResponseDTO = authenticationService.registerWithRole(loginRequest, userType);
        } catch (Exception e) {
            throw new BpmnError("Error_2i2nmgm", e.getMessage());
        }
        delegateExecution.setVariable("jwtResponseDTO", jwtResponseDTO);
        log.info("✅ Делегат RegisterDelegate, завершил свою работу");
    }
}
