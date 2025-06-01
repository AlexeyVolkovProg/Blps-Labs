package org.core.moderservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.core.moderservice.broker.ModerationStompProducer;
import org.core.moderservice.dto.broker.ResultStatus;
import org.core.moderservice.dto.broker.VideoModerationEventResult;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * Делегат отвечающий за отправку видео на ручную проверку
 */
@Slf4j
@RequiredArgsConstructor
@Component("sendMessageToCoreService")
public class SendMessageToCoreServiceDelegate implements JavaDelegate {
    private final ModerationStompProducer moderationStompProducer;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("✅ SendMessageToCoreServiceDelegate была успешно вызван");
        UUID videoID = (UUID) execution.getVariable("resultVideoId");
        Boolean resultStatus = (Boolean) execution.getVariable("resultStatus");
        VideoModerationEventResult videoModerationEventResult = new VideoModerationEventResult();
        videoModerationEventResult.setVideoId(videoID);
        if (resultStatus) {
            videoModerationEventResult.setResultStatus(ResultStatus.SUCCESS);
        }else{
            videoModerationEventResult.setResultStatus(ResultStatus.ERROR);
        }
        moderationStompProducer.sendMessageForModeration(videoModerationEventResult);
        log.info("✅ SendMessageToCoreServiceDelegate завершил свое выполнение");
    }
}
