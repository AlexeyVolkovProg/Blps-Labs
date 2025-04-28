package org.example.firstlabis.config;

import org.example.jira.JiraConnection;
import org.example.jira.JiraConnectionImpl;
import org.example.jira.JiraService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JiraConfig {
    @Value("${jira.url}")
    private String jiraUrl;

    @Value("${jira.username}")
    private String username;

    @Value("${jira.password}")
    private String password;

    @Bean
    public JiraConnection jiraConnection() {
        return new JiraConnectionImpl(jiraUrl, username, password);
    }

    @Bean
    public JiraService jiraService(JiraConnection jiraConnection) {
        return new JiraService(jiraConnection);
    }
}