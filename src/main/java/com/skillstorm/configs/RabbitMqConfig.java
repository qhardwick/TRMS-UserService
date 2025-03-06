package com.skillstorm.configs;

import com.skillstorm.constants.Queues;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${AWS_HOSTNAME:localhost}")
    private String host;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Exchanges:
    @Value("${exchanges.direct}")
    private String directExchange;

    // Set up credentials and connect to RabbitMQ:
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    // Configure the RabbitTemplate:
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setReplyTimeout(6000);
        return rabbitTemplate;
    }

    // Create the exchange:
    @Bean
    public Exchange directExchange() {
        return new DirectExchange(directExchange);
    }

    // Create the queues:

    // To Authentication-Service:
    @Bean
    public Queue registrationRequestQueue() {
        return new Queue(Queues.REGISTRATION_REQUEST.getQueue());
    }

    // From Authentication-Service:
    @Bean
    public Queue registrationResponseQueue() {
        return new Queue(Queues.REGISTRATION_RESPONSE.getQueue());
    }

    // From Form-Service:
    @Bean
    public Queue userLookupQueue() {
        return new Queue(Queues.USER_LOOKUP.toString());
    }
    @Bean
    public Queue supervisorLookupQueue() {
        return new Queue(Queues.SUPERVISOR_LOOKUP.getQueue());
    }

    @Bean
    public Queue departmentHeadLookupQueue() {
        return new Queue(Queues.DEPARTMENT_HEAD_LOOKUP.getQueue());
    }

    @Bean
    public Queue bencoLookupQueue() {
        return new Queue(Queues.BENCO_LOOKUP.getQueue());
    }

    @Bean
    public Queue adjustmentRequestQueue() {
        return new Queue(Queues.ADJUSTMENT_REQUEST.getQueue());
    }

    @Bean
    public Queue cancelRequestQueue() {
        return new Queue(Queues.CANCEL_REQUEST.getQueue());
    }

    // To Form-Service:
    @Bean
    public Queue userResponseQueue() {
        return new Queue(Queues.USER_REPONSE.toString());
    }

    @Bean
    public Queue supervisorResponseQueue() {
        return new Queue(Queues.SUPERVISOR_RESPONSE.getQueue());
    }

    @Bean
    public Queue departmentHeadResponseQueue() {
        return new Queue(Queues.DEPARTMENT_HEAD_RESPONSE.getQueue());
    }

    @Bean
    public Queue bencoResponseQueue() {
        return new Queue(Queues.BENCO_RESPONSE.getQueue());
    }

    @Bean
    public Queue adjustmentResponseQueue() {
        return new Queue(Queues.ADJUSTMENT_RESPONSE.getQueue());
    }


    // Bind the queues to the exchange:

    // To Authentication-Service:
    @Bean
    public Binding registrationRequestBinding(Queue registrationRequestQueue, Exchange directExchange) {
        return BindingBuilder.bind(registrationRequestQueue)
                .to(directExchange)
                .with(Queues.REGISTRATION_REQUEST)
                .noargs();
    }

    // From Authentication-Service:
    @Bean
    public Binding registrationResponseBinding(Queue registrationResponseQueue, Exchange directExchange) {
        return BindingBuilder.bind(registrationResponseQueue)
                .to(directExchange)
                .with(Queues.REGISTRATION_RESPONSE)
                .noargs();
    }

    // From Form-Service:
    @Bean
    public Binding userLookupBinding(Queue userLookupQueue, Exchange directExchange) {
        return BindingBuilder.bind(userLookupQueue)
                .to(directExchange)
                .with(Queues.USER_LOOKUP)
                .noargs();
    }

    @Bean
    public Binding supervisorLookupBinding(Queue supervisorLookupQueue, Exchange directExchange) {
        return BindingBuilder.bind(supervisorLookupQueue)
                .to(directExchange)
                .with(Queues.SUPERVISOR_LOOKUP)
                .noargs();
    }

    @Bean
    public Binding departmentHeadLookupBinding(Queue departmentHeadLookupQueue, Exchange directExchange) {
        return BindingBuilder.bind(departmentHeadLookupQueue)
                .to(directExchange)
                .with(Queues.DEPARTMENT_HEAD_LOOKUP)
                .noargs();
    }

    @Bean
    public Binding bencoLookupBinding(Queue bencoLookupQueue, Exchange directExchange) {
        return BindingBuilder.bind(bencoLookupQueue)
                .to(directExchange)
                .with(Queues.BENCO_LOOKUP)
                .noargs();
    }

    @Bean
    public Binding adjustmentRequestBinding(Queue adjustmentRequestQueue, Exchange directExchange) {
        return BindingBuilder.bind(adjustmentRequestQueue)
                .to(directExchange)
                .with(Queues.ADJUSTMENT_REQUEST)
                .noargs();
    }

    @Bean
    public Binding cancelRequestBinding(Queue cancelRequestQueue, Exchange directExchange) {
        return BindingBuilder.bind(cancelRequestQueue)
                .to(directExchange)
                .with(Queues.CANCEL_REQUEST)
                .noargs();
    }

    // To Form-Service:
    @Bean
    public Binding userResponseBinding(Queue userResponseQueue, Exchange directExchange) {
        return BindingBuilder.bind(userResponseQueue)
                .to(directExchange)
                .with(Queues.USER_REPONSE)
                .noargs();
    }

    @Bean
    public Binding supervisorResponseBinding(Queue supervisorResponseQueue, Exchange directExchange) {
        return BindingBuilder.bind(supervisorResponseQueue)
                .to(directExchange)
                .with(Queues.SUPERVISOR_RESPONSE)
                .noargs();
    }

    @Bean
    public Binding departmentHeadResponseBinding(Queue departmentHeadResponseQueue, Exchange directExchange) {
        return BindingBuilder.bind(departmentHeadResponseQueue)
                .to(directExchange)
                .with(Queues.DEPARTMENT_HEAD_RESPONSE)
                .noargs();
    }

    @Bean
    public Binding bencoResponseBinding(Queue bencoResponseQueue, Exchange directExchange) {
        return BindingBuilder.bind(bencoResponseQueue)
                .to(directExchange)
                .with(Queues.BENCO_RESPONSE)
                .noargs();
    }

    @Bean
    public Binding adjustmentResponseBinding(Queue adjustmentResponseQueue, Exchange directExchange) {
        return BindingBuilder.bind(adjustmentResponseQueue)
                .to(directExchange)
                .with(Queues.ADJUSTMENT_RESPONSE)
                .noargs();
    }
}