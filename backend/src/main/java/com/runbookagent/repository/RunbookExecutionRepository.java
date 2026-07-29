package com.runbookagent.repository;

import com.runbookagent.entity.ExecutionStatus;
import com.runbookagent.entity.RunbookExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunbookExecutionRepository extends JpaRepository<RunbookExecutionEntity, Long> {
    List<RunbookExecutionEntity> findByRunbookId(Long runbookId);
    List<RunbookExecutionEntity> findByStatus(ExecutionStatus status);
}
