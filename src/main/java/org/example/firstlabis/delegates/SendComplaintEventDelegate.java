package org.example.firstlabis.delegates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.domain.ComplaintCreateRequestDTO;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.service.domain.VideoService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component("sendComplaintEventDelegate")
@RequiredArgsConstructor
public class SendComplaintEventDelegate implements JavaDelegate {
    
    private final VideoService videoService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("✅ Был вызван SendComplaintEventDelegate, отвечающий за сохранение новой жалобы на видео");
        String videoId = (String) delegateExecution.getVariable("videoIdToComplain");
        String reason = (String) delegateExecution.getVariable("complainReason");

        var complaintRequestDTO = new ComplaintCreateRequestDTO();
        complaintRequestDTO.setVideoId(UUID.fromString(videoId));
        complaintRequestDTO.setReason(BlockReason.valueOf(reason));

        String username = (String) delegateExecution.getVariable("username");
        videoService.createNewComplaintForUser(complaintRequestDTO, username);
        log.info("✅ Делегат SendComplaintEventDelegate, завершил свою работу");
    }
}
