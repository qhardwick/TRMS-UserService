package com.skillstorm.services;

import com.skillstorm.constants.Role;
import com.skillstorm.dtos.ApproverDto;
import com.skillstorm.dtos.DepartmentDto;
import com.skillstorm.dtos.UserDto;
import com.skillstorm.exceptions.UserNotFoundException;
import com.skillstorm.repositories.UserRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentService departmentService;
    private final RabbitTemplate rabbitTemplate;
    private final MessageSource messageSource;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, DepartmentService departmentService, RabbitTemplate rabbitTemplate,  MessageSource messageSource) {
        this.userRepository = userRepository;
        this.departmentService = departmentService;
        this.rabbitTemplate = rabbitTemplate;
        this.messageSource = messageSource;
    }

    // Register new User:
    @Override
    public Mono<UserDto> register(UserDto newUser) {
        newUser.setEmail(newUser.getUsername().toLowerCase() + "@corporate.com");
        return userRepository.save(newUser.mapToEntity()).map(UserDto::new);
    }

    // Find User by Username:
    @Override
    public Mono<UserDto> findByUsername(String username) {
        return userRepository.findById(username).map(UserDto::new)
                .switchIfEmpty(Mono.error(new UserNotFoundException("user.not.found", username)));
    }

    // Find all Users:
    @Override
    public Flux<UserDto> findAll() {
        return userRepository.findAll().map(UserDto::new);
    }

    // Update User by Username:
    @Override
    public Mono<UserDto> updateUserByUsername(String username, UserDto updatedUser) {
        // First check if the user exists:
        return findByUsername(username).flatMap(foundUser -> {
            // Set the username in case it was not included in the request:
            updatedUser.setUsername(username);

            // Save to database and send back the response:
            return userRepository.save(updatedUser.mapToEntity())
                    .map(UserDto::new);
        });
    }

    // Delete User by Username:
    @Override
    public Mono<Void> deleteByUsername(String username) {
        // First check if the user exists:
        return findByUsername(username)
                .flatMap(foundUser -> userRepository.deleteById(username));
    }

    // Get Supervisor:
    @RabbitListener(queues = "supervisor-lookup-queue")
    public Mono<Void> findSupervisorByEmployeeUsername(@Payload String employeeUsername, @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                                    @Header(AmqpHeaders.REPLY_TO) String replyTo) {
        return findByUsername(employeeUsername)
                .flatMap(user -> findByUsername(user.getSupervisor()))
                .map(supervisor -> {

                    ApproverDto approver = new ApproverDto(supervisor.getUsername(), supervisor.getRole().name());
                    rabbitTemplate.convertAndSend(replyTo, approver, message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        return message;
                    });
                    return null;
                });
    }

    // Get Department Head:
    @RabbitListener(queues = "department-head-lookup-queue")
    public Mono<Void> findDepartmentHeadByEmployeeUsername(@Payload String employeeUsername, @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                                            @Header(AmqpHeaders.REPLY_TO) String replyTo) {

        return findByUsername(employeeUsername)
                .map(UserDto::getDepartment)
                .map(departmentService::findByName)
                .flatMap(departmentDtoMono -> departmentDtoMono.map(DepartmentDto::getHead))
                .map(departmentHead -> {
                    ApproverDto approver = new ApproverDto(departmentHead, Role.DEPARTMENT_HEAD.name());
                    rabbitTemplate.convertAndSend(replyTo, approver, message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        return message;
                    });
                    return null;
                });
    }

    // Get Benefits Coordinator. Placeholder for now:
    @RabbitListener(queues = "benco-lookup-queue")
    public Mono<Void> findBencoByEmployeeUsername(@Payload String employeeUsername, @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                                           @Header(AmqpHeaders.REPLY_TO) String replyTo) {

        return findByUsername(employeeUsername)
                .map(UserDto::getDepartment)
                .map(departmentService::findByName)
                .flatMap(departmentDtoMono -> departmentDtoMono.map(DepartmentDto::getHead))
                .map(departmentHead -> {
                    ApproverDto approver = new ApproverDto(departmentHead, Role.DEPARTMENT_HEAD.name());
                    rabbitTemplate.convertAndSend(replyTo, approver, message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        return message;
                    });
                    return null;
                });
    }
}
