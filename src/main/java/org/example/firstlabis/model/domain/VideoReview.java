package org.example.firstlabis.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.firstlabis.config.jpa.MapToJsonbConverter;
import org.example.firstlabis.model.domain.enums.BlockReason;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Сущность заявки видео на ревью
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoReview {
    @Id
    private UUID id = UUID.randomUUID();

    @Convert(converter = MapToJsonbConverter.class)
    private Map<BlockReason, Long> complaints = new HashMap<>();

    @OneToOne
    @JoinColumn(name = "video_id", nullable = false, unique = true)
    private Video video;
}
