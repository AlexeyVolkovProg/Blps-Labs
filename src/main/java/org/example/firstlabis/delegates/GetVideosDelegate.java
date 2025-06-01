package org.example.firstlabis.delegates;

import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.domain.VideoResponseDTO;
import org.example.firstlabis.service.domain.VideoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Slf4j
@Component("getVideosDelegate")
@RequiredArgsConstructor
public class GetVideosDelegate implements JavaDelegate {
    
    private final VideoService videoService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("✅ Был вызван GetVideosDelegate, отвечающий за доставание списка видео");
        var videos = videoService.getApprovedAndRejectedVideos();
    
        var videoTitles = videos.stream()
            .map(VideoResponseDTO::getTitle)
            .toList();

        delegateExecution.setVariable("videos", videos);
        delegateExecution.setVariable("videoTitles", videoTitles);

        log.info("✅ Делегат GetVideosDelegate, закончил свою работу");
    }
}
