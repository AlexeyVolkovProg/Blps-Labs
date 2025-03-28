package org.example.firstlabis.repository;

import org.example.firstlabis.model.domain.Video;
import org.example.firstlabis.model.domain.VideoReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoReviewRepository extends JpaRepository<VideoReview, UUID> {
    Optional<VideoReview> findByVideoId(UUID videoId);

    Optional<VideoReview> findByVideo(Video video);

    void deleteByVideoId(UUID id);
}
