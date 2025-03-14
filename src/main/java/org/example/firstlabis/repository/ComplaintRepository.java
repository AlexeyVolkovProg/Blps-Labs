package org.example.firstlabis.repository;

import org.example.firstlabis.model.domain.Complaint;
import org.example.firstlabis.model.domain.enums.BlockReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
    long countByVideoIdAndReason(UUID videoId, BlockReason reason);
}
