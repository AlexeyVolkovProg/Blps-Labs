package org.example.firstlabis.service.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.example.firstlabis.broker.ModerationConsumerJMS;
import org.example.firstlabis.broker.ModerationServiceStompProducer;
import org.example.firstlabis.dto.broker.VideoModerationEvent;
import org.example.firstlabis.dto.broker.VideoModerationEventResult;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.example.jira.JiraService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final EntityManager entityManager;
    private final VideoRepository videoRepository;
    private final ComplaintRepository complaintRepository;
    private final VideoReviewRepository videoReviewRepository;
    private final GenerateUrlUtil generateUrlUtil;
    private final JiraService jiraService;
    private final ModerationServiceStompProducer moderationServiceStompProducer;

    public VideoResponseDTO uploadVideo(VideoCreateRequestDTO videoDTO) {
        log.info("Uploading new video: {}", videoDTO.getTitle());
        Video video = new Video();
        video.setTitle(videoDTO.getTitle());
        video.setDescription(videoDTO.getDescription());
        video.setUrl(generateUrlUtil.generateVideoUrl(video.getId())); // генерируем нам ссылочку
        video.setStatus(VideoStatus.PENDING);// мокаем процесса проверки
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

    @Transactional
    public BlockReason autoModerateVideo(Video video) {
        BlockReason blockReason;

        if (video.getTitle().length() > 5) {
            blockReason = BlockReason.NOT_BLOCKED;
        } else {
            blockReason = BlockReason.PORNOGRAPHY;
        }

        log.info("Auto-moderated video {}: found block reason = {}", video.getId(), blockReason);

        return blockReason;
    }

    // Получение видео по ID
    public VideoResponseDTO getVideo(UUID videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        return convertToDTO(video);
    }

    public void createNewComplaint(ComplaintCreateRequestDTO complaintDTO) {
        createNewComplaintForUser(complaintDTO, SecurityUtil.getCurrentUsername());        
    }

    @Transactional
    public void createNewComplaintForUser(ComplaintCreateRequestDTO complaintDTO, String currentUsername) {
        Video video = videoRepository.findById(complaintDTO.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found"));

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
            newComplaint.setOwnerUsername(currentUsername);
            complaintRepository.save(newComplaint);
        }

        processedVideoReview(video);
    }

    /**
     * Метод отправляющий данные о видео в сервис модерации видео,
     * где на их основе будет создаваться заявка на модерацию видео
     * @param video рассматриваемое видео
     */
    private void processedVideoReview(Video video) {
        List<Complaint> complaints = complaintRepository.findByVideoId(video.getId());
        Map<BlockReason, Long> complaintsCount = complaints.stream()
                .collect(Collectors.groupingBy(Complaint::getReason, Collectors.counting()));
        if (complaintsCount.values().stream().anyMatch(count -> count >= 1)) {
            video.setStatus(VideoStatus.UNDER_REVIEW);
            videoRepository.save(video);
            moderationServiceStompProducer.sendMessageForModeration(new VideoModerationEvent(video.getId()));
        }
    }

    @Transactional
    public void createComplaintJiraTicket(VideoModerationEventResult eventResult) {
        log.info("Начинаем сознание тикета Jira для видео {}", eventResult.getVideoId());
        List<Complaint> complaints = complaintRepository.findByVideoId(eventResult.getVideoId());

        Map<BlockReason, Long> complaintsCount = complaints.stream()
                .collect(Collectors.groupingBy(Complaint::getReason, Collectors.counting()));
        String complaintsString = complaintsCount.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
        log.info("Мок, успешно создали джира тикет для видео", eventResult.getVideoId());
        String ticketKey = jiraService.createComplaintTicket(
            eventResult.getVideoId().toString(),
            "Multiple complaints received. Reasons: " + complaintsString
        );
        Video video = videoRepository.findById(eventResult.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found"));

        VideoReview videoReview = videoReviewRepository.findByVideo(video)
                .orElse(new VideoReview());

        videoReview.setJiraTicketKey(ticketKey); // todo не забудь вернуть значение
        videoReviewRepository.save(videoReview);
        log.info("Заканчиваем сознание тикета Jira для видео {}", eventResult.getVideoId());
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
        try {
            log.info("ЗАПУСТИЛИ МОДЕРАЦИЮ ВИДЕО {}", videoId);
            Video video = entityManager.find(Video.class, videoId);
            
            // Get the associated Jira ticket
            VideoReview videoReview = videoReviewRepository.findByVideo(video)
                .orElseThrow(() -> new RuntimeException("Video review not found"));
            
            if (approve) {
                log.info("АППРУВНУЛИ ВИДЕО {}", videoId);
                video.setStatus(VideoStatus.APPROVED);
                complaintRepository.deleteAllByVideoId(video.getId());
                videoReviewRepository.deleteByVideoId(video.getId());
            } else {
                log.info("НЕ АППРУВНУЛИ ВИДЕО {}", videoId);
                video.setStatus(VideoStatus.REJECTED);
                video.setBlockReason(blockReason);
            }

            if(blockReason == null){
                video.setBlockReason(BlockReason.PORNOGRAPHY);
            }
            // Update Jira ticket status
            if (videoReview.getJiraTicketKey() != null) {
                log.info("Мок. Успешно переместили в Done наш Jira Ticket");
                jiraService.markTicketAsDone(videoReview.getJiraTicketKey());
            }
            
            entityManager.merge(video);
        } catch (Exception e) {
            try {
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
                .owner(video.getOwnerUsername())
                .build();
    }
}
