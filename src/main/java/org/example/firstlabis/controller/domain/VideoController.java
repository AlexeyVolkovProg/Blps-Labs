package org.example.firstlabis.controller.domain;

import lombok.RequiredArgsConstructor;
import org.example.firstlabis.dto.domain.ComplaintCreateRequestDTO;
import org.example.firstlabis.dto.domain.VideoCreateRequestDTO;
import org.example.firstlabis.dto.domain.VideoResponseDTO;
import org.example.firstlabis.dto.domain.VideoReviewResponseDTO;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.service.domain.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<VideoResponseDTO> uploadVideo(@RequestBody VideoCreateRequestDTO videoDTO) {
        VideoResponseDTO savedVideo = videoService.uploadVideo(videoDTO);
        return new ResponseEntity<>(savedVideo, HttpStatus.CREATED);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<VideoResponseDTO> getVideo(@PathVariable UUID videoId) {
        VideoResponseDTO videoDTO = videoService.getVideo(videoId);
        return new ResponseEntity<>(videoDTO, HttpStatus.OK);
    }

    @PostMapping("/complaints")
    public ResponseEntity<Void> fileComplaint(@RequestBody ComplaintCreateRequestDTO complaintDTO) {
        videoService.newCreateComplaint(complaintDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/for-review")
    public ResponseEntity<List<VideoReviewResponseDTO>> getVideosForReview() {
        List<VideoReviewResponseDTO> videos = videoService.getVideosForReview();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    @PostMapping("/{videoId}/moderate")
    public ResponseEntity<Void> moderateVideo(@PathVariable UUID videoId,
                                              @RequestParam boolean approve,
                                              @RequestParam BlockReason blockReason) {
        videoService.moderateVideo(videoId, approve, blockReason);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/approved-rejected")
    public ResponseEntity<List<VideoResponseDTO>> getApprovedAndRejectedVideos() {
        List<VideoResponseDTO> videos = videoService.getApprovedAndRejectedVideos();
        return ResponseEntity.ok(videos);
    }
}