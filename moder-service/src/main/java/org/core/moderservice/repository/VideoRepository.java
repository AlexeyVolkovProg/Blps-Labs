package org.core.moderservice.repository;

import org.core.moderservice.model.domain.Video;
import org.core.moderservice.model.domain.enums.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    List<Video> findByStatus(VideoStatus status);
    List<Video> findByStatusIn(List<VideoStatus> statuses);
}
