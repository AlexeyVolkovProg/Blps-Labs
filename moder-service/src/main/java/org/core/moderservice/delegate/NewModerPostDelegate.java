package org.core.moderservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.core.moderservice.dto.broker.VideoModerationEvent;
import org.core.moderservice.service.ProcessVideoReviewService;
import org.springframework.stereotype.Component;


/**
 * Делегат, отвечающий за создание заявки на модерацию
 */
@Slf4j
@RequiredArgsConstructor
@Component("newModerPost")
public class NewModerPostDelegate implements JavaDelegate {

    private final ProcessVideoReviewService processVideoReviewService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("✅ Был вызван NewModerPostDelegate, отвечающий за создание новой заявки");
        VideoModerationEvent event = (VideoModerationEvent) execution.getVariable("VideoId");
        log.info("Запускаем создание заявки на видео с id {}", event.getVideoId());
        Boolean result = processVideoReviewService.processVideoReview(event);
        execution.setVariable("resultVideoId", event.getVideoId());
        execution.setVariable("resultStatus", result);
    }
}
