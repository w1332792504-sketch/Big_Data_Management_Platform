package com.datamanager.service;

import com.datamanager.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MessageQueueService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送任务执行消息到队列
     */
    public void sendTaskExecuteMessage(Long taskId, String taskName) {
        Map<String, Object> message = new HashMap<>();
        message.put("taskId", taskId);
        message.put("taskName", taskName);
        message.put("timestamp", System.currentTimeMillis());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TASK_EXCHANGE,
                RabbitMQConfig.TASK_ROUTING_KEY,
                message
        );
        log.info("任务执行消息已发送: taskId={}, taskName={}", taskId, taskName);
    }

    /**
     * 发送任务状态通知
     */
    public void sendTaskNotifyMessage(Long taskId, String taskName, String status, String detail) {
        Map<String, Object> message = new HashMap<>();
        message.put("taskId", taskId);
        message.put("taskName", taskName);
        message.put("status", status);
        message.put("detail", detail);
        message.put("timestamp", System.currentTimeMillis());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TASK_EXCHANGE,
                RabbitMQConfig.NOTIFY_ROUTING_KEY,
                message
        );
        log.info("任务通知已发送: taskId={}, status={}", taskId, status);
    }

    /**
     * 发送数据同步消息
     */
    public void sendSyncMessage(Long sourceId, Long targetId, String tableName) {
        Map<String, Object> message = new HashMap<>();
        message.put("sourceId", sourceId);
        message.put("targetId", targetId);
        message.put("tableName", tableName);
        message.put("timestamp", System.currentTimeMillis());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SYNC_EXCHANGE,
                RabbitMQConfig.SYNC_ROUTING_KEY,
                message
        );
        log.info("数据同步消息已发送: sourceId={}, tableName={}", sourceId, tableName);
    }

    /**
     * 监听任务执行队列
     */
    @RabbitListener(queues = RabbitMQConfig.TASK_QUEUE)
    public void handleTaskExecute(Map<String, Object> message) {
        log.info("收到任务执行消息: {}", message);
        // 任务执行逻辑由 ExtractTaskService 处理
    }

    /**
     * 监听任务通知队列
     */
    @RabbitListener(queues = RabbitMQConfig.NOTIFY_QUEUE)
    public void handleTaskNotify(Map<String, Object> message) {
        log.info("收到任务通知: {}", message);
        // 可扩展：发送邮件、钉钉、短信通知等
    }

    /**
     * 发送告警消息
     */
    public void sendAlertMessage(String alertContent) {
        Map<String, Object> message = new HashMap<>();
        message.put("content", alertContent);
        message.put("timestamp", System.currentTimeMillis());
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TASK_EXCHANGE,
                    "alert.notify",
                    message
            );
            log.info("告警消息已发送: {}", alertContent);
        } catch (Exception e) {
            log.warn("发送告警消息失败: {}", e.getMessage());
        }
    }

    /**
     * 监听数据同步队列
     */
    @RabbitListener(queues = RabbitMQConfig.SYNC_QUEUE)
    public void handleSyncExecute(Map<String, Object> message) {
        log.info("收到数据同步消息: {}", message);
        // 数据同步逻辑
    }
}
