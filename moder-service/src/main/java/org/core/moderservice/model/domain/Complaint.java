package org.example.firstlabis.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.firstlabis.model.audit.TrackEntity;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.model.security.User;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Complaint extends TrackEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Enumerated(EnumType.STRING)
    private BlockReason reason;
}