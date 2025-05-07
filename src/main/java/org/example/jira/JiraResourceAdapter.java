package org.example.jira;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.transaction.xa.XAResource;

import jakarta.resource.ResourceException;
import jakarta.resource.spi.ActivationSpec;
import jakarta.resource.spi.BootstrapContext;
import jakarta.resource.spi.Connector;
import jakarta.resource.spi.ResourceAdapter;
import jakarta.resource.spi.ResourceAdapterInternalException;
import jakarta.resource.spi.endpoint.MessageEndpointFactory;
import jakarta.resource.spi.work.WorkManager;

/**
 * Это основной класс адаптера ресурсов, который управляет жизненным циклом подключения к Jira.
 * 
 * Реализует интерфейс javax.resource.spi.ResourceAdapter.
 * Обычно содержит методы для инициализации, старта, остановки и очистки ресурсов адаптера.
 * 
 * @author amphyxs
 */
@Connector(displayName = "Jira Resource Adapter", vendorName = "Example", version = "1.0")
public class JiraResourceAdapter implements ResourceAdapter, Serializable {
    private static final Logger log = Logger.getLogger(JiraResourceAdapter.class.getName());
    private WorkManager workManager;

    @Override
    public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
        this.workManager = ctx.getWorkManager();
        log.info("Jira Resource Adapter started");
    }

    @Override
    public void stop() {
        log.info("Jira Resource Adapter stopped");
    }

    @Override
    public void endpointActivation(MessageEndpointFactory endpointFactory, ActivationSpec spec)
            throws ResourceException {
        // Not needed for this implementation
    }

    @Override
    public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
        // Not needed for this implementation
    }

    @Override
    public XAResource[] getXAResources(ActivationSpec[] specs) throws ResourceException {
        return new XAResource[0];
    }
}