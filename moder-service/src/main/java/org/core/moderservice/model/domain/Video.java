package org.core.moderservice.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.core.moderservice.model.audit.TrackEntity;
import org.core.moderservice.model.domain.enums.BlockReason;
import org.core.moderservice.model.domain.enums.VideoStatus;

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