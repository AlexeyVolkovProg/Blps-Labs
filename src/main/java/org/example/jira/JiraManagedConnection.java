package org.example.jira;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionEventListener;
import jakarta.resource.spi.ConnectionRequestInfo;
import jakarta.resource.spi.LocalTransaction;
import jakarta.resource.spi.ManagedConnection;
import jakarta.resource.spi.ManagedConnectionMetaData;

/**
 * Представляет физическое соединение с Jira, управляемое контейнером.
 *
 * Содержит методы для управления транзакциями, ассоциации с логическими соединениями, закрытия и очистки соединения.
 */
public class JiraManagedConnection implements ManagedConnection {
    private static final Logger log = Logger.getLogger(JiraManagedConnection.class.getName());
    private final JiraManagedConnectionFactory mcf;
    private JiraConnectionImpl connection;
    private Set<ConnectionEventListener> listeners;

    public JiraManagedConnection(JiraManagedConnectionFactory mcf) {
        this.mcf = mcf;
    }

    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        if (connection == null) {
            connection = new JiraConnectionImpl(
                    mcf.getJiraUrl(),
                    mcf.getUsername(),
                    mcf.getPassword());
        }
        return connection;
    }

    @Override
    public void destroy() throws ResourceException {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                throw new ResourceException("Failed to close connection", e);
            }
        }
    }

    @Override
    public void cleanup() throws ResourceException {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                throw new ResourceException("Failed to close connection", e);
            }
        }
        connection = null;
    }

    @Override
    public void associateConnection(Object connection) throws ResourceException {
        if (!(connection instanceof JiraConnectionImpl)) {
            throw new ResourceException("Invalid connection type");
        }
        this.connection = (JiraConnectionImpl) connection;
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public XAResource getXAResource() throws ResourceException {
        return null;
    }

    @Override
    public LocalTransaction getLocalTransaction() throws ResourceException {
        return null;
    }

    @Override
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        return new ManagedConnectionMetaData() {
            @Override
            public String getEISProductName() throws ResourceException {
                return "Jira";
            }

            @Override
            public String getEISProductVersion() throws ResourceException {
                return "1.0";
            }

            @Override
            public int getMaxConnections() throws ResourceException {
                return 0;
            }

            @Override
            public String getUserName() throws ResourceException {
                return mcf.getUsername();
            }
        };
    }

    @Override
    public void setLogWriter(PrintWriter out) throws ResourceException {
    }

    @Override
    public PrintWriter getLogWriter() throws ResourceException {
        return null;
    }
}