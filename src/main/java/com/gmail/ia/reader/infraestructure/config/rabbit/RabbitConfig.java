package com.gmail.ia.reader.infraestructure.config.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitConfig {

    // COLA GMAIL
    public static final String QUEUE = "gmail.queue";
    public static final String EXCHANGE = "gmail.exchange";
    public static final String ROUTING_KEY = "gmail.routing.key";

    public static final String DLQ_QUEUE = "gmail.queue.dlq";
    public static final String DLQ_EXCHANGE = "gmail.exchange.dlq";
    public static final String DLQ_ROUTING_KEY = "gmail.routingKey.dlq";


    // COLA DRIVE
    public static final String DRIVE_QUEUE = "drive.queue";
    public static final String DRIVE_EXCHANGE = "drive.exchange";
    public static final String DRIVE_ROUTING_KEY = "drive.routing.key";

    public static final String DRIVE_DLQ_QUEUE = "drive.queue.dlq";
    public static final String DRIVE_DLQ_EXCHANGE = "drive.exchange.dlq";
    public static final String DRIVE_DLQ_ROUTING_KEY = "drive.routing.key.dlq";


    @Bean
    public Queue queue() {
        return QueueBuilder
                .durable(QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder
                .durable(DLQ_QUEUE)
                .build();
    }

    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder
                .bind(dlqQueue())
                .to(dlqExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setPrefetchCount(1);
        factory.setConcurrentConsumers(4);
        factory.setMaxConcurrentConsumers(4);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    // DRIVE CONFIG

    @Bean
    public Queue driveQueue() {

        return QueueBuilder
                .durable(DRIVE_QUEUE)
                .withArgument(
                        "x-dead-letter-exchange",
                        DRIVE_DLQ_EXCHANGE
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        DRIVE_DLQ_ROUTING_KEY
                )
                .build();
    }

    @Bean
    public DirectExchange driveExchange() {
        return new DirectExchange(
                DRIVE_EXCHANGE
        );
    }

    @Bean
    public Binding driveBinding() {

        return BindingBuilder
                .bind(driveQueue())
                .to(driveExchange())
                .with(DRIVE_ROUTING_KEY);
    }

    @Bean
    public Queue driveDlqQueue() {

        return QueueBuilder
                .durable(DRIVE_DLQ_QUEUE)
                .build();
    }

    @Bean
    public DirectExchange driveDlqExchange() {

        return new DirectExchange(
                DRIVE_DLQ_EXCHANGE
        );
    }

    @Bean
    public Binding driveDlqBinding() {

        return BindingBuilder
                .bind(driveDlqQueue())
                .to(driveDlqExchange())
                .with(DRIVE_DLQ_ROUTING_KEY);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory driveRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(
                connectionFactory
        );

        factory.setMessageConverter(
                converter
        );

        factory.setPrefetchCount(1);

        factory.setConcurrentConsumers(8);

        factory.setMaxConcurrentConsumers(8);

        factory.setAcknowledgeMode(
                AcknowledgeMode.AUTO
        );

        return factory;
    }
}