package org.example.firstlabis.dto.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


/**
 * Сущность для отправки по ActiveMQ айди видео для последующего создания заявки на модерацию
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoModerationEvent {
    private UUID videoId;
}