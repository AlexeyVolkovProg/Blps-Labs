package org.example.firstlabis.repository;

import org.example.firstlabis.model.domain.Complaint;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
    long countByVideoIdAndReason(UUID videoId, BlockReason reason);
    void deleteAllByVideoId(UUID videoId);
    Optional<Complaint> findByVideoIdAndOwner_Id(UUID videoId, Long ownerId);
    List<Complaint> findByVideoId(UUID videoId);
}
