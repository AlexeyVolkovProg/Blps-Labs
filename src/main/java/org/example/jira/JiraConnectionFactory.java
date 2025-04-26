package org.example.jira;

import jakarta.resource.Referenceable;
import jakarta.resource.ResourceException;
import java.io.Serializable;

public interface JiraConnectionFactory extends Referenceable, Serializable {
    JiraConnection getConnection() throws ResourceException;
}