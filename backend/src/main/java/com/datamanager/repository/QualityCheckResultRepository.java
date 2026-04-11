package com.datamanager.repository;

import com.datamanager.model.QualityCheckResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QualityCheckResultRepository extends JpaRepository<QualityCheckResult, Long> {
    List<QualityCheckResult> findByRuleIdOrderByCheckTimeDesc(Long ruleId);
    Page<QualityCheckResult> findByDatasourceIdOrderByCheckTimeDesc(Long datasourceId, Pageable pageable);
    List<QualityCheckResult> findByCheckStatusAndCheckTimeAfter(String checkStatus, LocalDateTime checkTime);
    Long countByCheckStatus(String checkStatus);
}
