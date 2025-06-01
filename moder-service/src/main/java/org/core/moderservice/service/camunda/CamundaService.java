package org.core.moderservice.service.camunda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CamundaService {
    private final RuntimeService runtimeService;

    public void correlateMessage(
            String messageName,
            String businessKey,
            Map<String, Object> variables,
            Map<String, Object> correlatedVariables
    ) {
        log.atInfo()
                .setMessage("Correlating message " + messageName +
                        " to process by " + correlatedVariables +
                        " with variables " + variables
                )
                .addKeyValue("messageName", messageName)
                .addKeyValue("businessKey", businessKey)
                .addKeyValue("variables", variables)
                .addKeyValue("correlatedVariables", correlatedVariables)
                .log();
        MessageCorrelationBuilder messageCorrelationBuilder = runtimeService.createMessageCorrelation(messageName);
        if (businessKey != null) {
            messageCorrelationBuilder.processInstanceBusinessKey(businessKey);
        }
        if (correlatedVariables != null) {
            messageCorrelationBuilder.processInstanceVariablesEqual(correlatedVariables);
        }
        if (variables != null) {
            messageCorrelationBuilder.setVariables(variables);
        }
        messageCorrelationBuilder.correlateAll();
        log.info("✅ ОТПРАВИЛИ СООБЩЕНИЕ В КАМУНДУ");
    }
}
