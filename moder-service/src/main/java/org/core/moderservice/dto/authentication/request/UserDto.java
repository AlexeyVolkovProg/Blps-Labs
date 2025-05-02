package org.example.firstlabis.dto.authentication.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UserDto(
        @JsonProperty("id")
        Long id,

        @JsonProperty("username")
        String username,
        
        @JsonProperty("role")
        String role
) {
}
