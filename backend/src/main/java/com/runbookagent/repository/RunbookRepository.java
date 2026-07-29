package com.runbookagent.repository;

import com.runbookagent.entity.RunbookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RunbookRepository extends JpaRepository<RunbookEntity, Long> {
    Optional<RunbookEntity> findByName(String name);
}
