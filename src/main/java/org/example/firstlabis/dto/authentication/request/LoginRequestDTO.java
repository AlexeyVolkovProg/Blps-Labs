package org.example.firstlabis.dto.authentication.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequestDTO(
        @JsonProperty("username")
        String username,

        @JsonProperty("password")
        String password
) {
}
