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
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.example.firstlabis.model.domain.enums.VideoStatus;
import org.example.firstlabis.repository.ComplaintRepository;
import org.example.firstlabis.repository.VideoRepository;
import org.example.firstlabis.service.util.GenerateUrlUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    private final ComplaintRepository complaintRepository;

    private final GenerateUrlUtil generateUrlUtil;

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
        return VideoStatus.REJECTED;
    }

    // Получение видео по ID
    public VideoResponseDTO getVideo(UUID videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));
        return convertToDTO(video);
    }

    // Жалоба на видео
    @Transactional
    public void createComplaint(ComplaintCreateRequestDTO complaintDTO) {
        Video video = videoRepository.findById(complaintDTO.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found"));
        Complaint complaint = new Complaint();
        complaint.setVideo(video);
        complaint.setReason(complaintDTO.getReason());
        complaintRepository.save(complaint);
        entityManager.flush();   // Синхронизируем изменения с БД
        entityManager.clear();   // Очистим кэш первого уровня для обновления данных
        long complaintCount = complaintRepository.countByVideoIdAndReason(complaintDTO.getVideoId()
                , complaintDTO.getReason());
        if (complaintCount > 3) {
            video.setStatus(VideoStatus.UNDER_REVIEW);
            videoRepository.save(video);
        }
    }

    // Получить все видео на повторной проверке
    public List<VideoReviewResponseDTO> getVideosForReview() {
        List<Video> videos = videoRepository.findByStatus(VideoStatus.UNDER_REVIEW);
        return videos.stream().map(this::mapVideoToReviewDTO).collect(Collectors.toList());
    }

    // Принятие или отклонение видео модераторами
    @Transactional
    public void moderateVideo(UUID videoId, boolean approve) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        if (approve) {
            video.setStatus(VideoStatus.APPROVED);
        } else {
            video.setStatus(VideoStatus.REJECTED);
        }
        videoRepository.save(video);
    }

    @Transactional
    public VideoReviewResponseDTO mapVideoToReviewDTO(Video video) {
        List<Complaint> complaints = video.getComplaints();
        Map<BlockReason, Long> complaintsCount = complaints.stream()
                .collect(Collectors.groupingBy(
                        Complaint::getReason,
                        Collectors.counting()
                ));
        return VideoReviewResponseDTO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .status(video.getStatus())
                .url(video.getUrl())
                .complaintsCount(complaintsCount).build();
    }

    private VideoResponseDTO convertToDTO(Video video) {
        return VideoResponseDTO.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .url(video.getUrl())
                .status(video.getStatus().name())
                .build();
    }
}
