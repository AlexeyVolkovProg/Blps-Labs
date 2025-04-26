package org.example.jira;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.gson.Gson;

@Component
public class JiraConnectionImpl implements JiraConnection {
    private final JiraRestClient jiraRestClient;
    private final Gson gson = new Gson();

    public JiraConnectionImpl(
            @Value("${jira.url}") String jiraUrl,
            @Value("${jira.username}") String username,
            @Value("${jira.password}") String password) {
        this.jiraRestClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(jiraUrl), username, password);
    }

    @Override
    public String createIssue(String summary, String description, String projectKey) throws IOException {
        try {
            IssueInput issueInput = new IssueInputBuilder()
                    .setProjectKey(projectKey)
                    .setSummary(summary)
                    .setDescription(description)
                    .setIssueTypeId(10001L) // Task type
                    .build();

            BasicIssue issue = jiraRestClient.getIssueClient()
                    .createIssue(issueInput)
                    .get();

            return gson.toJson(issue);
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("Failed to create Jira issue", e);
        }
    }

    @Override
    public void transitionIssue(String issueKey, String transitionId) throws IOException {
        try {
            Issue issue = jiraRestClient.getIssueClient()
                    .getIssue(issueKey)
                    .get();

            TransitionInput transitionInput = new TransitionInput(Integer.parseInt(transitionId));
            jiraRestClient.getIssueClient()
                    .transition(issue, transitionInput)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("Failed to transition Jira issue", e);
        }
    }

    @Override
    public String getIssue(String issueKey) throws IOException {
        try {
            Issue issue = jiraRestClient.getIssueClient()
                    .getIssue(issueKey)
                    .get();
            return gson.toJson(issue);
        } catch (InterruptedException | ExecutionException e) {
            throw new IOException("Failed to get Jira issue", e);
        }
    }

    @Override
    public void close() throws IOException {
        jiraRestClient.close();
    }
}