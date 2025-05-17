package org.example.firstlabis.delegates;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.service.domain.VideoService;

import lombok.RequiredArgsConstructor;

@Named("getVideosDelegate")
@RequiredArgsConstructor
public class GetVideosDelegate implements JavaDelegate {
    
    private final VideoService videoService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        var videos = videoService.getApprovedAndRejectedVideos();
    
        var videoTitles = videos.stream()
            .map(video -> video.getTitle())
            .toList();

        delegateExecution.setVariable("videos", videos);
        delegateExecution.setVariable("videoTitles", videoTitles);

    }
}
