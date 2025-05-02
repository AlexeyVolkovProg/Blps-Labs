package org.core.moderservice.broker;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.core.moderservice.dto.broker.VideoModerationEventResult;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Компонент для отправки сообщений по протоколу STOMP в ActiveMQ
 */
@Slf4j
@Component
public class ModerationStompProducer {

    @Value("${spring.activemq.broker-url}")
    String brokerUri;
    @Value("${topic.name.moder-service-response}")
    String topicName;

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    @PostConstruct
    public void init() {
        try {
            System.out.println("Подгрузили: " + brokerUri);
            System.out.println("Подгрузили: " + topicName);
            StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
            factory.setBrokerURI(brokerUri);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            producer = session.createProducer(topic);
            log.info("✅ Producer для отправки в топик {} брокера {} успешно инициализирован", topicName, brokerUri);
        } catch (JMSException e) {
            log.info("JMS error {}", e.getMessage());
        }
    }

    public void sendMessageForModeration(VideoModerationEventResult videoModerationEventResult) {
        try {
            String payload = new ObjectMapper().writeValueAsString(videoModerationEventResult);
            TextMessage message = session.createTextMessage(payload);
            producer.send(message);
            log.info("\uD83D\uDCE4 Отправлено: {}", logEventInfo(videoModerationEventResult));
        } catch (JMSException e) {
            log.info("Ошибка JMS {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.info("Произошла ошибка parsing JSON {}", e.getMessage());
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (producer != null) producer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
            System.out.println("Ресурсы продюсера успешно закрыты");
        } catch (JMSException e) {
            log.info(e.getMessage());
        }
    }

    private String logEventInfo(VideoModerationEventResult videoModerationEvent) {
        return "Сообщение c TS: " + LocalDateTime.now() + " c UUID: " + videoModerationEvent.getVideoId();
    }
}
