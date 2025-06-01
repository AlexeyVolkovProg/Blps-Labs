package org.example.firstlabis.delegates.hands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.domain.VideoReviewResponseDTO;
import org.example.firstlabis.service.domain.VideoService;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("getModerVideo")
@RequiredArgsConstructor
public class GetModerVideoDelegate implements JavaDelegate {
    private final VideoService videoService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("✅ Был вызван GetModerVideoDelegate, достающей видео, подлежащее модерации");
        List<VideoReviewResponseDTO> videos = videoService.getVideosForReview();
        if (!videos.isEmpty()) {
            execution.setVariable("selectedVideoId", videos.get(0).getVideoId());
        }
    }
}
