package org.example.firstlabis.dto.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VideoCreateRequestDTO {
    private String title;
    private String description;
}
