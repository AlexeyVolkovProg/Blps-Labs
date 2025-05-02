package org.example.firstlabis.broker;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.example.firstlabis.dto.broker.VideoModerationEvent;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Компонент для отправки сообщений по протоколу STOMP в ActiveMQ
 */
@Slf4j
@Component
public class ModerationServiceStompProducer {

    @Value("${spring.activemq.broker-url}")
    String brokerUri;
    @Value("${topic.name.moder-service-request}")
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

    public void sendMessageForModeration(VideoModerationEvent videoModerationEvent) {
        try {
            String payload = new ObjectMapper().writeValueAsString(videoModerationEvent);
            TextMessage message = session.createTextMessage(payload);
            producer.send(message);
            log.info("\uD83D\uDCE4 Отправлено: {}", logEventInfo(videoModerationEvent));
        } catch (JMSException e) {
            log.info("Ошибка JMS {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.info("Произошла ошибка parsing JSON {}", e.getMessage());
        }
    }

//    @EventListener(ApplicationReadyEvent.class)
//    @Scheduled(fixedDelay = 15000)
//    public void sendMessageForModerationTest() {
//        try {
//            if (session == null || producer == null) {
//                log.warn("Продюсер не инициализирован. Пропускаем отправку тестового сообщения.");
//                return;
//            }
//            VideoModerationEvent videoModerationEvent =
//                    new VideoModerationEvent(UUID.fromString("1c5b74ba-784d-49f4-b881-4db03ae9efed"));
//            String payload = new ObjectMapper().writeValueAsString(videoModerationEvent);
//            TextMessage message = session.createTextMessage(payload);
//            producer.send(message);
//            log.info("\uD83D\uDCE4 Отправлено: {}", logEventInfo(videoModerationEvent));
//        } catch (JMSException e) {
//            log.info("Ошибка JMS {}", e.getMessage());
//        } catch (JsonProcessingException e) {
//            log.info("Произошла ошибка parsing JSON {}", e.getMessage());
//        }
//    }

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

    private String logEventInfo(VideoModerationEvent videoModerationEvent) {
        return "Сообщение c TS: " + LocalDateTime.now() + " c UUID: " + videoModerationEvent.getVideoId();
    }
}
