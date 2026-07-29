package com.runbookagent.repository;

import com.runbookagent.entity.StepExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StepExecutionRepository extends JpaRepository<StepExecutionEntity, Long> {
    List<StepExecutionEntity> findByExecutionIdOrderByStepNumberAsc(Long executionId);
    Optional<StepExecutionEntity> findByExecutionIdAndStepNumber(Long executionId, Integer stepNumber);
}
