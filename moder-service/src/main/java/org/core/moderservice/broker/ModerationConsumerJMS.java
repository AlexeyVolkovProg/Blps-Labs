package org.core.moderservice.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.core.moderservice.dto.broker.VideoModerationEvent;
import org.core.moderservice.service.ProcessVideoReviewService;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationConsumerJMS implements MessageListener {

    @Value("${spring.activemq.broker-url}")
    String brokerUri;
    @Value("${topic.name.moder-service-request}")
    String topicName;

    private Connection connection;
    private Session session;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProcessVideoReviewService processVideoReviewService;

    @PostConstruct
    public void init() {
        try {
            StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
            factory.setBrokerURI(brokerUri);
            connection = factory.createConnection();
            connection.setClientID("consumer-client-1"); // заюзаем для durable подписки
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createDurableSubscriber(topic, "sub1");
            consumer.setMessageListener(this);
            log.info("✅ Consumer успешно подписался на топик {} брокера {}", topicName, brokerUri);
        } catch (JMSException e) {
           log.info(e.getMessage());
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String body = textMessage.getText();
                VideoModerationEvent event = objectMapper.readValue(body, VideoModerationEvent.class);
                log.info("✅ Получено сообщение о видео: {}", event.getVideoId());
                processVideoReviewService.processVideoReview(event);
            }
        } catch (JsonProcessingException e) {
            log.info("Исключение parsing JSON {}", e.getMessage());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    @PreDestroy
    public void cleanup() {
        try {
            if (session != null) session.close();
            if (connection != null) connection.close();
            System.out.println("🧹 Ресурсы consumer закрыты.");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
