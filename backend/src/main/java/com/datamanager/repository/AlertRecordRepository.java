package com.datamanager.repository;

import com.datamanager.model.AlertRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {
    Page<AlertRecord> findByStatusOrderByAlertTimeDesc(String status, Pageable pageable);
    Page<AlertRecord> findByAlertLevelOrderByAlertTimeDesc(String alertLevel, Pageable pageable);
    List<AlertRecord> findByStatusAndAlertTimeAfterOrderByAlertTimeDesc(String status, LocalDateTime alertTime);

    @Query("SELECT COUNT(a) FROM AlertRecord a WHERE a.status = 'PENDING'")
    Long countPendingAlerts();

    @Query("SELECT COUNT(a) FROM AlertRecord a WHERE a.alertLevel = 'CRITICAL' AND a.status = 'PENDING'")
    Long countCriticalPendingAlerts();
}
