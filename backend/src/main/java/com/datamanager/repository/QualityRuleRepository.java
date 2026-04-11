package com.datamanager.repository;

import com.datamanager.model.QualityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualityRuleRepository extends JpaRepository<QualityRule, Long> {
    List<QualityRule> findByDatasourceIdOrderByCreateTimeDesc(Long datasourceId);
    List<QualityRule> findByStatusOrderByCreateTimeDesc(Integer status);
    List<QualityRule> findByRuleTypeOrderByCreateTimeDesc(String ruleType);
}
