package org.example.firstlabis.service.domain;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.firstlabis.dto.domain.ComplaintCreateRequestDTO;
import org.example.firstlabis.dto.domain.VideoCreateRequestDTO;
import org.example.firstlabis.dto.domain.VideoResponseDTO;
import org.example.firstlabis.dto.domain.VideoReviewResponseDTO;
import org.example.firstlabis.model.domain.Complaint;
import org.example.firstlabis.model.domain.Video;
import org.example.firstlabis.model.domain.VideoReview;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.model.domain.enums.VideoStatus;
import org.example.firstlabis.repository.ComplaintRepository;
import org.example.firstlabis.repository.VideoRepository;
import org.example.firstlabis.repository.VideoReviewRepository;
import org.example.firstlabis.service.util.GenerateUrlUtil;
import org.example.firstlabis.service.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    private final ComplaintRepository complaintRepository;

    private final VideoReviewRepository videoReviewRepository;

    private final GenerateUrlUtil generateUrlUtil;

    private final SecurityUtil securityUtil;

    private final EntityManager entityManager;

    // Загрузка видео
    public VideoResponseDTO uploadVideo(VideoCreateRequestDTO videoDTO) {
        Video video = new Video();
        video.setTitle(videoDTO.getTitle());
        video.setDescription(videoDTO.getDescription());
        video.setUrl(generateUrlUtil.generateVideoUrl(video.getId())); // генерируем нам ссылочку
        video.setStatus(autoModerateVideo(video));// мокаем процесса проверки
        video = videoRepository.save(video);
        return convertToDTO(video);
    }

    private VideoStatus autoModerateVideo(Video video) {
        if (video.getTitle().length() > 5) {
            return VideoStatus.APPROVED;
        }
        video.setBlockReason(BlockReason.PORNOGRAPHY);
        return VideoStatus.REJECTED;
    }

    // Получение видео по ID
    public VideoResponseDTO getVideo(UUID videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        return convertToDTO(video);
    }


    @Transactional
    public void newCreateComplaint(ComplaintCreateRequestDTO complaintDTO) {
        Video video = videoRepository.findById(complaintDTO.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found"));

        Optional<Complaint> existingComplaint = complaintRepository.findByVideoIdAndOwner_Id(
                complaintDTO.getVideoId(), SecurityUtil.getCurrentUserId());

        if (existingComplaint.isPresent()) {
            Complaint complaint = existingComplaint.get();
            complaint.setReason(complaintDTO.getReason());
            complaintRepository.save(complaint);
        } else {
            Complaint newComplaint = new Complaint();
            newComplaint.setVideo(video);
            newComplaint.setReason(complaintDTO.getReason());
            complaintRepository.save(newComplaint);
        }
        updateVideoReview(video);
    }

    private void updateVideoReview(Video video) {
        List<Complaint> complaints = complaintRepository.findByVideoId(video.getId());

        Map<BlockReason, Long> complaintsCount = complaints.stream()
                .collect(Collectors.groupingBy(Complaint::getReason, Collectors.counting()));

        if (complaintsCount.values().stream().anyMatch(count -> count > 1)) {
            video.setStatus(VideoStatus.UNDER_REVIEW);
            videoRepository.save(video);

            VideoReview videoReview = videoReviewRepository.findByVideo(video)
                    .orElse(new VideoReview());

            videoReview.setVideo(video);
            videoReview.setComplaints(
                    complaintsCount.entrySet().stream()
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue))
            );
            videoReviewRepository.save(videoReview);
        }
    }


    // Получить все заявки на повторной проверке
    @Transactional
    public List<VideoReviewResponseDTO> getVideosForReview() {
        entityManager.flush();
        entityManager.clear();
        List<VideoReview> videoReviews = videoReviewRepository.findAll();
        return videoReviews.stream().map(this::mapVideoReviewToDTO).collect(Collectors.toList());
    }


    // Принятие или отклонение видео модераторами
    @Transactional
    public void moderateVideo(UUID videoId, boolean approve, BlockReason blockReason) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        if (approve) {
            video.setStatus(VideoStatus.APPROVED);
            complaintRepository.deleteAllByVideoId(video.getId());
            videoReviewRepository.deleteByVideoId(video.getId());
        } else {
            video.setStatus(VideoStatus.REJECTED);
            video.setBlockReason(blockReason);
        }
        videoRepository.save(video);
    }

    @Transactional
    public List<VideoResponseDTO> getApprovedAndRejectedVideos() {
        List<Video> videos = videoRepository.findByStatusIn(List.of(VideoStatus.APPROVED, VideoStatus.REJECTED));
        return videos.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    private VideoReviewResponseDTO mapVideoReviewToDTO(VideoReview videoReview) {
        Video video = videoReview.getVideo();
        return VideoReviewResponseDTO.builder()
                .videoId(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .status(video.getStatus())
                .url(video.getUrl())
                .complaintsSummary(videoReview.getComplaints())
                .build();
    }


    private VideoResponseDTO convertToDTO(Video video) {
        return VideoResponseDTO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .url(video.getUrl())
                .status(video.getStatus().name())
                .blockReason(video.getBlockReason().name())
                .build();
    }
}
