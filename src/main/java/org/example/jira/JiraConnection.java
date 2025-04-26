package org.example.jira;

import java.io.Closeable;
import java.io.IOException;

public interface JiraConnection extends Closeable {
    String createIssue(String summary, String description, String projectKey) throws IOException;

    void transitionIssue(String issueKey, String transitionId) throws IOException;

    String getIssue(String issueKey) throws IOException;
}