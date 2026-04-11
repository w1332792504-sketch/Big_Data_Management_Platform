package com.datamanager.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 任务执行队列
    public static final String TASK_QUEUE = "data.task.execute";
    public static final String TASK_EXCHANGE = "data.task.exchange";
    public static final String TASK_ROUTING_KEY = "task.execute";

    // 任务状态通知队列
    public static final String NOTIFY_QUEUE = "data.task.notify";
    public static final String NOTIFY_EXCHANGE = "data.task.exchange";
    public static final String NOTIFY_ROUTING_KEY = "task.notify";

    // 数据同步队列
    public static final String SYNC_QUEUE = "data.sync.execute";
    public static final String SYNC_EXCHANGE = "data.sync.exchange";
    public static final String SYNC_ROUTING_KEY = "sync.execute";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 任务执行交换机和队列
    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(TASK_QUEUE).build();
    }

    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(TASK_EXCHANGE);
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, DirectExchange taskExchange) {
        return BindingBuilder.bind(taskQueue).to(taskExchange).with(TASK_ROUTING_KEY);
    }

    // 任务通知交换机和队列
    @Bean
    public Queue notifyQueue() {
        return QueueBuilder.durable(NOTIFY_QUEUE).build();
    }

    @Bean
    public Binding notifyBinding(Queue notifyQueue, DirectExchange taskExchange) {
        return BindingBuilder.bind(notifyQueue).to(taskExchange).with(NOTIFY_ROUTING_KEY);
    }

    // 数据同步交换机和队列
    @Bean
    public Queue syncQueue() {
        return QueueBuilder.durable(SYNC_QUEUE).build();
    }

    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(SYNC_EXCHANGE);
    }

    @Bean
    public Binding syncBinding(Queue syncQueue, DirectExchange syncExchange) {
        return BindingBuilder.bind(syncQueue).to(syncExchange).with(SYNC_ROUTING_KEY);
    }
}
