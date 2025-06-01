package org.example.firstlabis.delegates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.model.domain.enums.VideoStatus;
import org.example.firstlabis.repository.VideoRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component("blockVideoDelegate")
@RequiredArgsConstructor
public class BlockVideoDelegate implements JavaDelegate {
    private final VideoRepository videoRepository;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("✅ Был вызван BlockVideoDelegate, отвечающий за блокировку видео");
        UUID videoId = (UUID) delegateExecution.getVariable("moderatedVideoId");
        BlockReason blockReason = (BlockReason) delegateExecution.getVariable("moderatedBlockReason");
        var video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        video.setStatus(VideoStatus.REJECTED);
        video.setBlockReason(blockReason);
        videoRepository.save(video);
        log.info("✅ Делегат BlockVideoDelegate, закончил свою работу");
    }
}
