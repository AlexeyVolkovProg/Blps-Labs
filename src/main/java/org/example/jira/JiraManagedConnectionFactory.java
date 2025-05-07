package org.example.jira;

import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionDefinition;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionFactory;

/**
 * Фабрика управляемых соединений. Создаёт объекты соединения с Jira, которые могут управляться контейнером приложений.
 * 
 * Реализует интерфейс javax.resource.spi.ManagedConnectionFactory.
 * Содержит логику создания физических соединений (JiraManagedConnection) и фабрик соединений (JiraConnectionFactoryImpl).
 * 
 * @author amphyxs
 */
@ConnectionDefinition(connectionFactory = JiraConnectionFactory.class,
        connectionFactoryImpl = JiraConnectionFactoryImpl.class,
        connection = JiraConnection.class,
        connectionImpl = JiraConnectionImpl.class)
public class JiraManagedConnectionFactory implements ManagedConnectionFactory, Serializable {
    private static final Logger log = Logger.getLogger(JiraManagedConnectionFactory.class.getName());
    private String jiraUrl;
    private String username;
    private String password;

    public String getJiraUrl() {
        return jiraUrl;
    }

    public void setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        return new JiraConnectionFactoryImpl(this, cxManager);
    }

    @Override
    public Object createConnectionFactory() throws ResourceException {
        throw new ResourceException("This resource adapter doesn't support non-managed environments");
    }

    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        return new JiraManagedConnection(this);
    }

    @Override
    public ManagedConnection matchManagedConnections(@SuppressWarnings("rawtypes") Set connections,
                                                     Subject subject,
                                                     ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        return null;
    }

    @Override
    public void setLogWriter(java.io.PrintWriter out) throws ResourceException {
    }

    @Override
    public java.io.PrintWriter getLogWriter() throws ResourceException {
        return null;
    }
}