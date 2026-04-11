package com.datamanager.repository;

import com.datamanager.model.TaskExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
    Page<TaskExecution> findByTaskIdOrderByStartTimeDesc(Long taskId, Pageable pageable);
    List<TaskExecution> findByTaskIdAndStatusOrderByStartTimeDesc(Long taskId, String status);
    List<TaskExecution> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    long countByStatus(String status);
    List<TaskExecution> findTop10ByOrderByStartTimeDesc();
}
