package org.example.firstlabis.dto.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.firstlabis.model.domain.enums.BlockReason;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModerationRequestDTO {
    private UUID videoId;
    private Map<BlockReason, Long> complaints;
}
