package org.example.firstlabis.delegates.hands;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.broker.VideoModerationEventResult;
import org.example.firstlabis.service.domain.VideoService;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component("moderateAndClearDelegate")
public class ModerateAndClearDelegate implements JavaDelegate {
    private final VideoService videoService;
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("✅ Был вызван ModerateAndClearDelegate, которая применяет модерацию и удаляет жалобы после этого");
        String verdict = (String) execution.getVariable("videoVerdict");
        boolean value = Boolean.parseBoolean(verdict);
        log.info("Получили значение {}", verdict);
        log.info("Спарсили значение {}", value);
        VideoModerationEventResult event = (VideoModerationEventResult) execution.getVariable("Event");
        videoService.moderateVideo(event.getVideoId(), value, null);
    }
}
