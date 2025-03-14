package org.example.firstlabis.service.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateUrlUtil {
    private final String DEFAULT_VIDEO_URL = "https://videoservice.com/video/";

    public String generateVideoUrl(UUID videoId) {
        return DEFAULT_VIDEO_URL + videoId + ".mp4";
    }
}
