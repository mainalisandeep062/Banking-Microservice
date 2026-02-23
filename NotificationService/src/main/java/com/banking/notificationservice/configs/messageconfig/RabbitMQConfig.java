package com.banking.notificationservice.configs.messageconfig;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMQConfig {

    // Names used across the microservices
    public static final String EXCHANGE = "banking.direct.exchange";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.key";
    public static final String USER_SYNC_QUEUE = "user.sync.queue";
    public static final String USER_SYNC_ROUTING_KEY = "user.sync.key";
    public static final String ACCOUNT_SYNC_QUEUE = "account.sync.queue";
    public static final String ACCOUNT_SYNC_ROUTING_KEY = "account.sync.key";
    public static final String TRANSACTION_SYNC_QUEUE = "transaction.sync.queue";
    public static final String TRANSACTION_SYNC_ROUTING_KEY = "transaction.sync.key";

    // 1. Define the Exchange
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    // 2. Define the Queue (Durable = true means it survives RabbitMQ restarts)
    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    // 3. Create the Binding between Queue and Exchange
    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange exchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(exchange)
                .with(NOTIFICATION_ROUTING_KEY);
    }

    // 4. JSON Converter: Automatically converts Java Objects to JSON for the queue
    @Bean
    public MessageConverter jsonMessageConverter() {
        // Using the Jackson 3 builder pattern
        JsonMapper jsonMapper = JsonMapper.builder()
                .findAndAddModules() // Automatically discovers JavaTimeModule for dates
                .build();

        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean public Queue userSyncQueue() {return new Queue(USER_SYNC_QUEUE, true);}
    @Bean public Binding userSyncBinding(Queue userSyncQueue, DirectExchange exchange) {
            return BindingBuilder.bind(userSyncQueue).to(exchange).with(USER_SYNC_ROUTING_KEY);}

    @Bean public Queue accountSyncQueue() {return new Queue(ACCOUNT_SYNC_QUEUE, true);}
    @Bean public Binding accountSyncBinding(Queue accountSyncQueue, DirectExchange exchange) {
        return BindingBuilder.bind(accountSyncQueue).to(exchange).with(ACCOUNT_SYNC_ROUTING_KEY);}

    @Bean public Queue transactionSyncQueue(){return new Queue(TRANSACTION_SYNC_QUEUE, true);}
    @Bean public Binding transactionSyncBinding(Queue transactionSyncQueue, DirectExchange exchange){
        return BindingBuilder.bind(transactionSyncQueue).to(exchange).with(TRANSACTION_SYNC_ROUTING_KEY);}
}