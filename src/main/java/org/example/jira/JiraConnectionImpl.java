package org.example.jira;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

/**
 * Implementation of logical connection to Jira.
 * 
 * Contains business logic for interacting with Jira through REST API.
 * Uses Spring's RestTemplate for HTTP requests.
 */
@Component
public class JiraConnectionImpl implements JiraConnection {
    private final String jiraUrl;
    private final String authHeader;
    private final RestTemplate restTemplate;
    private final Gson gson = new Gson();

    public JiraConnectionImpl(
            @Value("${jira.url}") String jiraUrl,
            @Value("${jira.username}") String username,
            @Value("${jira.password}") String password) {
        // Ensure the URL ends with a slash
        this.jiraUrl = jiraUrl.endsWith("/") ? jiraUrl : jiraUrl + "/";
        this.restTemplate = new RestTemplate();
        
        // Create Basic Auth header
        String auth = username + ":" + password;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    @Override
    public String createIssue(String summary, String description, String projectKey) throws IOException {
        try {
            Map<String, Object> fields = new HashMap<>();
            fields.put("project", Map.of("key", projectKey));
            fields.put("summary", summary);
            fields.put("description", description);
            fields.put("issuetype", Map.of("id", "10002")); // Task type

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fields", fields);

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                jiraUrl + "rest/api/2/issue",
                HttpMethod.POST,
                request,
                Map.class
            );

            return response.getBody().get("id").toString();
        } catch (Exception e) {
            throw new IOException("Failed to create Jira issue", e);
        }
    }

    @Override
    public void transitionIssue(String issueKey, String transitionId) throws IOException {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("transition", Map.of("id", transitionId));

            HttpHeaders headers = createHeaders();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            restTemplate.exchange(
                jiraUrl + "rest/api/2/issue/" + issueKey + "/transitions",
                HttpMethod.POST,
                request,
                Void.class
            );
        } catch (Exception e) {
            throw new IOException("Failed to transition Jira issue", e);
        }
    }

    @Override
    public String getIssue(String issueKey) throws IOException {
        try {
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                jiraUrl + "rest/api/2/issue/" + issueKey,
                HttpMethod.GET,
                request,
                Map.class
            );

            return gson.toJson(response.getBody());
        } catch (Exception e) {
            throw new IOException("Failed to get Jira issue", e);
        }
    }

    @Override
    public void close() throws IOException {
        // No resources to close when using RestTemplate
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}