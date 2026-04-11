package com.datamanager.service;

import com.datamanager.exception.NotFoundException;
import com.datamanager.model.AlertRecord;
import com.datamanager.model.AlertRule;
import com.datamanager.repository.AlertRecordRepository;
import com.datamanager.repository.AlertRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 告警服务
 */
@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    @Autowired
    private AlertRuleRepository alertRuleRepository;

    @Autowired
    private AlertRecordRepository alertRecordRepository;

    @Autowired
    private MessageQueueService messageQueueService;

    // 告警规则管理
    public List<AlertRule> findAllRules() {
        return alertRuleRepository.findAll();
    }

    public List<AlertRule> findActiveRules() {
        return alertRuleRepository.findByStatusOrderByCreateTimeDesc(1);
    }

    public AlertRule findRuleById(Long id) {
        return alertRuleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("告警规则不存在：" + id));
    }

    @Transactional
    public AlertRule saveRule(AlertRule rule) {
        return alertRuleRepository.save(rule);
    }

    @Transactional
    public void deleteRule(Long id) {
        alertRuleRepository.deleteById(id);
    }

    @Transactional
    public AlertRule updateRuleStatus(Long id, Integer status) {
        AlertRule rule = findRuleById(id);
        rule.setStatus(status);
        return alertRuleRepository.save(rule);
    }

    // 发送告警
    @Transactional
    public AlertRecord sendAlert(Long ruleId, String content) {
        AlertRule rule = findRuleById(ruleId);

        AlertRecord record = new AlertRecord();
        record.setRuleId(ruleId);
        record.setRuleName(rule.getRuleName());
        record.setAlertType(rule.getAlertType());
        record.setAlertLevel(rule.getAlertLevel());
        record.setAlertContent(content);
        record.setNotifyChannel(rule.getNotifyChannel());
        record.setStatus("PENDING");
        record.setAlertTime(LocalDateTime.now());

        alertRecordRepository.save(record);

        // 异步发送通知
        try {
            sendNotification(record, rule.getNotifyConfig());
            record.setStatus("SENT");
            record.setNotifyTime(LocalDateTime.now());
        } catch (Exception e) {
            log.error("发送告警通知失败：{}", record.getId(), e);
        }

        return alertRecordRepository.save(record);
    }

    private void sendNotification(AlertRecord record, String notifyConfig) {
        String channel = record.getNotifyChannel();
        String message = String.format("[%s] %s: %s",
                record.getAlertLevel(), record.getRuleName(), record.getAlertContent());

        switch (channel) {
            case "EMAIL":
                // TODO: 发送邮件
                log.info("发送邮件告警：{}", message);
                break;
            case "SMS":
                // TODO: 发送短信
                log.info("发送短信告警：{}", message);
                break;
            case "WEBHOOK":
                // TODO: 调用Webhook
                log.info("发送Webhook告警：{}", message);
                break;
            default:
                // 发送到消息队列
                messageQueueService.sendAlertMessage(message);
        }
    }

    // 快捷告警方法
    @Transactional
    public void alertTaskFail(String taskName, String errorMsg) {
        List<AlertRule> rules = alertRuleRepository.findByAlertTypeOrderByCreateTimeDesc("TASK_FAIL");
        for (AlertRule rule : rules) {
            if (rule.getStatus() == 1) {
                sendAlert(rule.getId(), String.format("任务 %s 执行失败：%s", taskName, errorMsg));
            }
        }
    }

    @Transactional
    public void alertTaskTimeout(String taskName, long duration) {
        List<AlertRule> rules = alertRuleRepository.findByAlertTypeOrderByCreateTimeDesc("TASK_TIMEOUT");
        for (AlertRule rule : rules) {
            if (rule.getStatus() == 1) {
                sendAlert(rule.getId(), String.format("任务 %s 执行超时，耗时 %d 秒", taskName, duration));
            }
        }
    }

    @Transactional
    public void alertQualityFail(String tableName, double failRate) {
        List<AlertRule> rules = alertRuleRepository.findByAlertTypeOrderByCreateTimeDesc("QUALITY_FAIL");
        for (AlertRule rule : rules) {
            if (rule.getStatus() == 1) {
                sendAlert(rule.getId(), String.format("表 %s 数据质量检查失败，失败率 %.2f%%", tableName, failRate));
            }
        }
    }

    // 告警记录查询
    public Page<AlertRecord> findRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return alertRecordRepository.findAll(pageable);
    }

    public Page<AlertRecord> findPendingRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return alertRecordRepository.findByStatusOrderByAlertTimeDesc("PENDING", pageable);
    }

    public Long countPendingAlerts() {
        return alertRecordRepository.countPendingAlerts();
    }

    public Long countCriticalPendingAlerts() {
        return alertRecordRepository.countCriticalPendingAlerts();
    }

    // 告警确认
    @Transactional
    public AlertRecord acknowledge(Long id, String acknowledgedBy) {
        AlertRecord record = alertRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("告警记录不存在：" + id));
        record.setStatus("ACKNOWLEDGED");
        record.setAcknowledgedBy(acknowledgedBy);
        record.setAcknowledgeTime(LocalDateTime.now());
        return alertRecordRepository.save(record);
    }

    // 告警解决
    @Transactional
    public AlertRecord resolve(Long id, String resolvedBy, String remark) {
        AlertRecord record = alertRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("告警记录不存在：" + id));
        record.setStatus("RESOLVED");
        record.setResolvedBy(resolvedBy);
        record.setResolveTime(LocalDateTime.now());
        record.setRemark(remark);
        return alertRecordRepository.save(record);
    }
}
