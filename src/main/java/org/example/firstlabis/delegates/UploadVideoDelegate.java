package org.example.firstlabis.delegates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.domain.VideoCreateRequestDTO;
import org.example.firstlabis.service.domain.VideoService;
import org.springframework.stereotype.Component;

@Slf4j
@Component("uploadVideoDelegate")
@RequiredArgsConstructor
public class UploadVideoDelegate implements JavaDelegate {
    private final VideoService videoService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("✅ Был вызван UploadVideoDelegate, отвечающий за загрузку видео");
        String videoTitle = (String) execution.getVariable("title");
        String videoDescription = (String) execution.getVariable("description");
        log.info("Полученные данные title {}, description {}", videoTitle, videoDescription);
        videoService.uploadVideo(createVideo(videoTitle, videoDescription));
    }

    public VideoCreateRequestDTO createVideo(String videoTitle, String videoDescription) {
        return new VideoCreateRequestDTO(videoTitle, videoDescription);
    }
}
