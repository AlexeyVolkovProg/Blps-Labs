package org.example.jira;

import java.io.Serializable;

import javax.naming.Reference;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ConnectionManager;
import jakarta.resource.spi.ManagedConnectionFactory;

public class JiraConnectionFactoryImpl implements JiraConnectionFactory, Serializable {
    private final ManagedConnectionFactory mcf;
    private final ConnectionManager cm;
    private Reference reference;

    public JiraConnectionFactoryImpl(ManagedConnectionFactory mcf, ConnectionManager cm) {
        this.mcf = mcf;
        this.cm = cm;
    }

    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }

    @Override
    public Reference getReference() {
        return reference;
    }

    @Override
    public JiraConnection getConnection() throws ResourceException {
        return (JiraConnection) cm.allocateConnection(mcf, null);
    }
}