package org.example.firstlabis.dto.domain;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VideoResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private UUID id;
    private String title;
    private String description;
    private String url;
    private String status;
    private String blockReason;
    private String owner;
}
