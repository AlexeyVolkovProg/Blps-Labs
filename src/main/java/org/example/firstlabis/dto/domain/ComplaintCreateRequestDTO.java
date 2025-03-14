package org.example.firstlabis.dto.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.firstlabis.model.domain.enums.BlockReason;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ComplaintCreateRequestDTO {
    private UUID videoId;
    private BlockReason reason;
}
