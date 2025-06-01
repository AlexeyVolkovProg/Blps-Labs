package org.core.moderservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.core.moderservice.broker.ModerationStompProducer;
import org.core.moderservice.dto.broker.ResultStatus;
import org.core.moderservice.dto.broker.VideoModerationEvent;
import org.core.moderservice.dto.broker.VideoModerationEventResult;
import org.core.moderservice.model.domain.Complaint;
import org.core.moderservice.model.domain.Video;
import org.core.moderservice.model.domain.VideoReview;
import org.core.moderservice.model.domain.enums.BlockReason;
import org.core.moderservice.repository.ComplaintRepository;
import org.core.moderservice.repository.VideoRepository;
import org.core.moderservice.repository.VideoReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessVideoReviewService {
    private final VideoRepository videoRepository;
    private final ComplaintRepository complaintRepository;
    private final VideoReviewRepository videoReviewRepository;
    private final ModerationStompProducer moderationStompProducer;

    /**
     * Метод подсчета кол-ва жалоб по разным прецедентам и создания заявки на модерацию видео
     * @param videoModerationEvent событие уведомляющее о необходимости создания заявки на ревью видео
     */
    @Transactional
    public Boolean processVideoReview(VideoModerationEvent videoModerationEvent) {
        Video video = videoRepository.findById(videoModerationEvent.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found"));
        try {
            log.info("Начало обработки видео {} ", videoModerationEvent.getVideoId());
            VideoReview videoReview = videoReviewRepository.findByVideo(video)
                    .orElse(new VideoReview());

            List<Complaint> complaints = complaintRepository.findByVideoId(video.getId());
            Map<BlockReason, Long> complaintsCount = complaints.stream()
                    .collect(Collectors.groupingBy(Complaint::getReason, Collectors.counting()));

            videoReview.setVideo(video);
            videoReview.setComplaints(
                    complaintsCount.entrySet().stream()
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    Map.Entry::getValue))
            );
            videoReviewRepository.save(videoReview);
            log.info("Логика обработки видео {} успешно отработал", videoModerationEvent.getVideoId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
