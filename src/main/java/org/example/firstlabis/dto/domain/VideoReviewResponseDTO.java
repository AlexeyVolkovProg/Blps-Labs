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
    private UUID id;
    private String title;
    private String description;
    private String url;
    private VideoStatus status;
    private Map<BlockReason, Long> complaintsCount;
}
