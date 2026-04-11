package com.datamanager.repository;

import com.datamanager.model.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {
    List<AlertRule> findByStatusOrderByCreateTimeDesc(Integer status);
    List<AlertRule> findByAlertTypeOrderByCreateTimeDesc(String alertType);
    List<AlertRule> findByAlertLevelOrderByCreateTimeDesc(String alertLevel);
}
