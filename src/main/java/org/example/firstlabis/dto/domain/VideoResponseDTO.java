package org.example.firstlabis.dto.domain;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VideoResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private String url;
    private String status;
    private String blockReason;
}
