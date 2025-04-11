package org.example.firstlabis.service.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.SynchronizationType;
import jakarta.transaction.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final EntityManager entityManager;
    private final EntityManagerFactory entityManagerFactory;
    private final VideoRepository videoRepository;
    private final ComplaintRepository complaintRepository;
    private final VideoReviewRepository videoReviewRepository;
    private final GenerateUrlUtil generateUrlUtil;
    private final UserTransaction userTransaction;


    // Загрузка видео
    public VideoResponseDTO uploadVideo(VideoCreateRequestDTO videoDTO) {
        log.info("Uploading new video: {}", videoDTO.getTitle());
        Video video = new Video();
        video.setTitle(videoDTO.getTitle());
        video.setDescription(videoDTO.getDescription());
        video.setUrl(generateUrlUtil.generateVideoUrl(video.getId())); // генерируем нам ссылочку
        video.setStatus(autoModerateVideo(video));// мокаем процесса проверки

        // Set owner username directly before saving
        try {
            String currentUsername = SecurityUtil.getCurrentUsername();
            log.info("Setting video owner to: {}", currentUsername);
            video.setOwnerUsername(currentUsername);
        } catch (Exception e) {
            log.error("Error getting current username: {}", e.getMessage());
            video.setOwnerUsername("anonymous");
        }

        video = videoRepository.save(video);
        log.info("Video saved successfully with ID: {}", video.getId());
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

        // Get current username
        String currentUsername = SecurityUtil.getCurrentUsername();

        // Modify repository to find by username instead of ID
        Optional<Complaint> existingComplaint = complaintRepository.findByVideoIdAndOwnerUsername(
                complaintDTO.getVideoId(), currentUsername);

        if (existingComplaint.isPresent()) {
            Complaint complaint = existingComplaint.get();
            complaint.setReason(complaintDTO.getReason());
            complaintRepository.save(complaint);
        } else {
            Complaint newComplaint = new Complaint();
            newComplaint.setVideo(video);
            newComplaint.setReason(complaintDTO.getReason());
            // Set owner username directly
            newComplaint.setOwnerUsername(currentUsername);
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

    public void moderateVideo(UUID videoId, boolean approve, BlockReason blockReason) {
        try {
            userTransaction.begin();
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            Video video = entityManager.find(Video.class, videoId);
            if (approve) {
                video.setStatus(VideoStatus.APPROVED);
                complaintRepository.deleteAllByVideoId(video.getId());
                videoReviewRepository.deleteByVideoId(video.getId());
            } else {
                video.setStatus(VideoStatus.REJECTED);
                video.setBlockReason(blockReason);
            }
            entityManager.merge(video);
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
                log.warn("Transaction rolled back : {}", e.getMessage());
            } catch (Exception rollbackEx) {
                log.error("Rollback also failed: {}", rollbackEx.getMessage(), rollbackEx);
            }
            throw new RuntimeException("Transaction failed", e);
        }
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
                .owner(video.getOwnerUsername()) // Include owner username in DTO
                .build();
    }
}
