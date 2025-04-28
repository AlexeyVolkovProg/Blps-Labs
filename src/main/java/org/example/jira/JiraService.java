package org.example.jira;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JiraService {
    private final JiraConnection jiraConnection;

    @Autowired
    public JiraService(JiraConnection jiraConnection) {
        this.jiraConnection = jiraConnection;
    }

    public String createComplaintTicket(String videoId, String complaintReason) {
        try {
            String summary = "Complaint for video " + videoId;
            String description = "Complaint reason: " + complaintReason;
            return jiraConnection.createIssue(summary, description, "COMP");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Jira ticket", e);
        }
    }

    public void markTicketAsDone(String issueKey) {
        try {
            jiraConnection.transitionIssue(issueKey, "21");
        } catch (Exception e) {
            throw new RuntimeException("Failed to transition Jira ticket", e);
        }
    }
}