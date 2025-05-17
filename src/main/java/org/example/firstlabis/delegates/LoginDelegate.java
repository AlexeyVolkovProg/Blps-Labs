package org.example.firstlabis.delegates;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.authentication.request.LoginRequestDTO;
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
        var jwtResponseDTO = authenticationService.authenticate(loginRequest);
    
        delegateExecution.setVariable("jwtResponseDTO", jwtResponseDTO);
    }
}
