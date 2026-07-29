package com.runbookagent.repository;

import com.runbookagent.entity.ApprovalRequestEntity;
import com.runbookagent.entity.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequestEntity, Long> {
    List<ApprovalRequestEntity> findByExecutionId(Long executionId);
    Optional<ApprovalRequestEntity> findByStepExecutionId(Long stepExecutionId);
    List<ApprovalRequestEntity> findByStatus(ApprovalStatus status);
}
