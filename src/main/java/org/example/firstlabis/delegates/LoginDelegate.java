package org.example.firstlabis.delegates;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.authentication.request.LoginRequestDTO;
import org.example.firstlabis.dto.authentication.response.JwtResponseDTO;
import org.example.firstlabis.service.security.jwt.AuthenticationService;

import javax.inject.Named;

@Named("loginDelegate")
@RequiredArgsConstructor
public class LoginDelegate implements JavaDelegate {
    
    private final AuthenticationService authenticationService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String username = (String) delegateExecution.getVariable("username");
        String password = (String) delegateExecution.getVariable("password");

        var loginRequest = new LoginRequestDTO(username, password);
        JwtResponseDTO jwtResponseDTO;
        try {
            jwtResponseDTO = authenticationService.authenticate(loginRequest);
        } catch (Exception e) {
            throw new BpmnError("Error_21aqs7q", e.getMessage());
        }
    
        delegateExecution.setVariable("jwtResponseDTO", jwtResponseDTO);
    }
}
