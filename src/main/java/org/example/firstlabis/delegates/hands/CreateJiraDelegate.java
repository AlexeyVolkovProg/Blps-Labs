package org.example.firstlabis.delegates.hands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.broker.VideoModerationEventResult;
import org.example.firstlabis.service.domain.VideoService;
import org.springframework.stereotype.Component;

@Slf4j
@Component("createJiraDelegate")
@RequiredArgsConstructor
public class CreateJiraDelegate implements JavaDelegate {
    private final VideoService videoService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("✅ Был вызван CreateJiraDelegate, отвечающий за создание заявки на модерацию видео");
        VideoModerationEventResult event = (VideoModerationEventResult) execution.getVariable("Event");
        log.info("Получилось достать идентификатор видео {}", event.getVideoId());
        videoService.createComplaintJiraTicket(event);
    }
}
