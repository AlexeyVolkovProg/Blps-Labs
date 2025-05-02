package org.example.firstlabis.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.firstlabis.dto.broker.ResultStatus;
import org.example.firstlabis.dto.broker.VideoModerationEventResult;
import org.example.firstlabis.service.domain.VideoService;
import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationConsumerJMS implements MessageListener {

    @Value("${spring.activemq.broker-url}")
    String brokerUri;
    @Value("${topic.name.moder-service-response}")
    String topicName;

    private Connection connection;
    private Session session;

    private final VideoService videoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
            factory.setBrokerURI(brokerUri);
            connection = factory.createConnection();
            connection.setClientID("consumer-client-3"); // заюзаем для durable подписки
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
                VideoModerationEventResult event = objectMapper.readValue(body, VideoModerationEventResult.class);
                log.info("✅ Получено сообщение о результате создания заявки на модерацию видео: {}", event.getVideoId());
                if (ResultStatus.SUCCESS.equals(event.getResultStatus())){
                    videoService.createComplaintJiraTicket(event);
                }
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
