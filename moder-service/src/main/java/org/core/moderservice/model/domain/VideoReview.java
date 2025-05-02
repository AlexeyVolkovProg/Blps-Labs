package org.example.firstlabis.model.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.example.firstlabis.model.domain.enums.BlockReason;

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

    @ElementCollection
    @CollectionTable(name = "video_review_complaints", 
        joinColumns = @JoinColumn(name = "video_review_id"))
    @MapKeyColumn(name = "reason")
    @Column(name = "count")
    private Map<BlockReason, Long> complaints;

    @Column(name = "jira_ticket_key")
    private String jiraTicketKey;
}
