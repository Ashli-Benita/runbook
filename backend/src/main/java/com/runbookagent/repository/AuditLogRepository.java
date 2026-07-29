package com.runbookagent.repository;

import com.runbookagent.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    List<AuditLogEntity> findByExecutionIdOrderByTimestampAsc(Long executionId);
    List<AuditLogEntity> findTop50ByOrderByTimestampDesc();
}
