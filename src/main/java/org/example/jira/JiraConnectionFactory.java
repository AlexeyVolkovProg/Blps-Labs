package org.example.jira;

import jakarta.resource.Referenceable;
import jakarta.resource.ResourceException;
import java.io.Serializable;

/**
 * Интерфейс фабрики логических соединений для приложений.
 * 
 * Обычно содержит метод для получения логического соединения (JiraConnection).
 */
public interface JiraConnectionFactory extends Referenceable, Serializable {
    JiraConnection getConnection() throws ResourceException;
}