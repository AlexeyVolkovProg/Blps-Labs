package org.core.moderservice.repository;

import org.core.moderservice.model.domain.Complaint;
import org.core.moderservice.model.domain.enums.BlockReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
    long countByVideoIdAndReason(UUID videoId, BlockReason reason);
    void deleteAllByVideoId(UUID videoId);
    Optional<Complaint> findByVideoIdAndOwnerUsername(UUID videoId, String ownerUsername);
    List<Complaint> findByVideoId(UUID videoId);
}
