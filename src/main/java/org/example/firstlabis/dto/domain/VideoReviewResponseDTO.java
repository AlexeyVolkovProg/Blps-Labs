package org.example.firstlabis.dto.domain;

import lombok.*;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.model.domain.enums.VideoStatus;

import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VideoReviewResponseDTO {
    private UUID videoId;
    private String title;
    private String description;
    private VideoStatus status;
    private String url;
    private Map<BlockReason, Long> complaintsSummary;
}
