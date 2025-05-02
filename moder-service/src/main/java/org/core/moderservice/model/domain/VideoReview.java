package org.core.moderservice.model.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.core.moderservice.model.domain.enums.BlockReason;
import org.core.moderservice.util.MapToJsonbConverter;

import java.util.Map;
import java.util.UUID;


/**
 * Сущность заявки видео на ревью
 */
@Entity
@Data
@Table(name = "video_reviews")
public class VideoReview {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "count")
    @Convert(converter = MapToJsonbConverter.class)
    private Map<BlockReason, Long> complaints;

    @Column(name = "jira_ticket_key")
    private String jiraTicketKey;
}
