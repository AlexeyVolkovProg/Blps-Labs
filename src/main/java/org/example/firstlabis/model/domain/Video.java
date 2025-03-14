package org.example.firstlabis.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.firstlabis.model.domain.enums.VideoStatus;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Video {

    @Id
    private UUID id = UUID.randomUUID();

    private String title;
    private String description;
    private String url;

    @Enumerated(EnumType.STRING)
    private VideoStatus status = VideoStatus.PENDING;

    @OneToMany(mappedBy = "video")
    private List<Complaint> complaints;
}