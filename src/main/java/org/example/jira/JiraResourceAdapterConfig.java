package org.example.jira;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JiraResourceAdapterConfig {
    @Value("${jira.url}")
    private String jiraUrl;

    @Value("${jira.username}")
    private String username;

    @Value("${jira.password}")
    private String password;

    @Bean
    public JiraManagedConnectionFactory jiraManagedConnectionFactory() {
        JiraManagedConnectionFactory factory = new JiraManagedConnectionFactory();
        factory.setJiraUrl(jiraUrl);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public JiraConnectionFactory jiraConnectionFactory(JiraManagedConnectionFactory mcf) {
        return new JiraConnectionFactoryImpl(mcf, null);
    }
}