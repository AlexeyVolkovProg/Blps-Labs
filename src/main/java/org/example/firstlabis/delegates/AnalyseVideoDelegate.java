package org.example.firstlabis.delegates;

import java.util.List;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.example.firstlabis.model.domain.Video;
import org.example.firstlabis.model.domain.enums.VideoStatus;
import org.example.firstlabis.repository.VideoRepository;
import org.example.firstlabis.service.domain.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("analyseVideoDelegate")
@RequiredArgsConstructor
public class AnalyseVideoDelegate implements JavaDelegate {
    
    private final VideoService videoService;
    private final VideoRepository videoRepository;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("✅ Был вызван AnalyseVideoDelegate, отвечающий за автоматическую модерацию видео");
        List<Video> pendingVideos = videoRepository.findByStatus(VideoStatus.PENDING);
        if (pendingVideos.isEmpty()) {
            log.info("No pending videos found");
            throw new BpmnError("Error_2pk21pi", "No pending videos available for analysis");
        }
        var video = pendingVideos.get(0);
        var blockReason = videoService.autoModerateVideo(video);
        delegateExecution.setVariable("moderatedBlockReason", blockReason);
        delegateExecution.setVariable("moderatedVideoId", video.getId());
        log.info("✅ Делегат AnalyseVideoDelegate закончил свою работу");
    }
}
