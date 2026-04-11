package com.datamanager.repository;

import com.datamanager.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByModuleOrderByCreateTimeDesc(String module, Pageable pageable);
    Page<AuditLog> findByOperatorOrderByCreateTimeDesc(String operator, Pageable pageable);
    Page<AuditLog> findByCreateTimeBetweenOrderByCreateTimeDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.createTime >= ?1")
    Long countByCreateTimeAfter(LocalDateTime createTime);

    @Query("SELECT a.module, COUNT(a) FROM AuditLog a GROUP BY a.module")
    List<Object[]> countGroupByModule();
}
