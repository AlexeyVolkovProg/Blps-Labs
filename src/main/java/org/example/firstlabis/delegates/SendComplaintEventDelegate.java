package org.example.firstlabis.delegates;

import java.util.UUID;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.example.firstlabis.dto.domain.ComplaintCreateRequestDTO;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.service.domain.VideoService;

import lombok.RequiredArgsConstructor;

@Named("sendComplaintEventDelegate")
@RequiredArgsConstructor
public class SendComplaintEventDelegate implements JavaDelegate {
    
    private final VideoService videoService;
    
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String videoId = (String) delegateExecution.getVariable("videoIdToComplain");
        String reason = (String) delegateExecution.getVariable("complainReason");

        var complaintRequestDTO = new ComplaintCreateRequestDTO();
        complaintRequestDTO.setVideoId(UUID.fromString(videoId));
        complaintRequestDTO.setReason(BlockReason.valueOf(reason));

        String username = (String) delegateExecution.getVariable("username");
        videoService.createNewComplaintForUser(complaintRequestDTO, username);
    }
}
