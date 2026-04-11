package com.datamanager.controller;

import com.datamanager.dto.ApiResponse;
import com.datamanager.config.RabbitMQConfig;
import com.datamanager.service.MessageQueueService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mq")
public class MessageQueueController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageQueueService messageQueueService;

    /**
     * 获取队列状态信息
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getQueueStatus() {
        Map<String, Object> status = new HashMap<>();

        // 队列信息
        Map<String, Object> taskQueue = new HashMap<>();
        taskQueue.put("name", RabbitMQConfig.TASK_QUEUE);
        taskQueue.put("description", "任务执行队列");
        try {
            Object taskCount = rabbitTemplate.receiveAndConvert(RabbitMQConfig.TASK_QUEUE, 100);
            taskQueue.put("hasMessages", taskCount != null);
        } catch (Exception e) {
            taskQueue.put("hasMessages", false);
        }

        Map<String, Object> notifyQueue = new HashMap<>();
        notifyQueue.put("name", RabbitMQConfig.NOTIFY_QUEUE);
        notifyQueue.put("description", "任务通知队列");

        Map<String, Object> syncQueue = new HashMap<>();
        syncQueue.put("name", RabbitMQConfig.SYNC_QUEUE);
        syncQueue.put("description", "数据同步队列");

        status.put("taskQueue", taskQueue);
        status.put("notifyQueue", notifyQueue);
        status.put("syncQueue", syncQueue);
        status.put("connected", true);

        return ApiResponse.success(status);
    }

    /**
     * 发送测试消息
     */
    @PostMapping("/test")
    public ApiResponse<String> sendTestMessage(@RequestBody Map<String, String> body) {
        String queue = body.getOrDefault("queue", "task");
        String message = body.getOrDefault("message", "test message");

        switch (queue) {
            case "task" -> messageQueueService.sendTaskExecuteMessage(0L, message);
            case "notify" -> messageQueueService.sendTaskNotifyMessage(0L, message, "TEST", "测试通知");
            case "sync" -> messageQueueService.sendSyncMessage(0L, 0L, message);
        }
        return ApiResponse.success("消息已发送到 " + queue + " 队列");
    }
}
