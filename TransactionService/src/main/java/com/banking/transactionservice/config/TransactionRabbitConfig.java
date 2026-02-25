package com.banking.transactionservice.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionRabbitConfig {

    public static final String EXCHANGE = "banking.direct.exchange";
    public static final String USER_SYNC_ROUTING_KEY = "transaction.sync.key";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        // Using Jackson 3 builder for Spring Boot 4.0
        tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder()
                .findAndAddModules()
                .build();
        return new JacksonJsonMessageConverter(mapper);
    }
}