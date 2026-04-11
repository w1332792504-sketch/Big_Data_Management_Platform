package com.datamanager.repository;

import com.datamanager.model.ExtractTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtractTaskRepository extends JpaRepository<ExtractTask, Long> {
    List<ExtractTask> findAllByOrderByCreateTimeDesc();
    List<ExtractTask> findByStatusOrderByCreateTimeDesc(Integer status);
    boolean existsByTaskName(String taskName);
}
