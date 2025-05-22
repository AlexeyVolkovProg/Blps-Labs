package org.example.firstlabis.delegates;

import java.util.UUID;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.model.domain.enums.VideoStatus;
import org.example.firstlabis.repository.VideoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Named("publishVideoDelegate")
@RequiredArgsConstructor
@Slf4j
public class PublishVideoDelegate implements JavaDelegate {
    private final VideoRepository videoRepository;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        UUID videoId = (UUID) delegateExecution.getVariable("moderatedVideoId");
        BlockReason blockReason = (BlockReason) delegateExecution.getVariable("moderatedBlockReason");
    
        var video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        video.setStatus(VideoStatus.APPROVED);
        video.setBlockReason(blockReason);
        videoRepository.save(video);
    }
}
