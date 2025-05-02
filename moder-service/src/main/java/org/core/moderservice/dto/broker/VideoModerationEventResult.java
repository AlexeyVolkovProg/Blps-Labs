package org.core.moderservice.dto.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoModerationEventResult {
    private UUID videoId;
    ResultStatus resultStatus;
}
