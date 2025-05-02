package org.example.firstlabis.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.firstlabis.model.audit.TrackEntity;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.model.domain.enums.VideoStatus;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Video extends TrackEntity {

    @Id
    private UUID id = UUID.randomUUID();

    private String title;
    private String description;
    private String url;

    @Enumerated(EnumType.STRING)
    private VideoStatus status = VideoStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private BlockReason blockReason = BlockReason.NOT_BLOCKED;

    @OneToMany(mappedBy = "video")
    private List<Complaint> complaints;
}