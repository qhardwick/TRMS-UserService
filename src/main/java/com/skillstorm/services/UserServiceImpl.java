package com.skillstorm.services;

import com.skillstorm.constants.Queues;
import com.skillstorm.constants.Role;
import com.skillstorm.dtos.*;
import com.skillstorm.exceptions.UserNotFoundException;
import com.skillstorm.repositories.UserRepository;
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
import reactor.core.publisher.MonoSink;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentService departmentService;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, MonoSink<CredentialsDto>> registrationCorrelationMap;
    private final MessageSource messageSource;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, DepartmentService departmentService, RabbitTemplate rabbitTemplate,  MessageSource messageSource) {
        this.userRepository = userRepository;
        this.departmentService = departmentService;
        this.rabbitTemplate = rabbitTemplate;
        this.registrationCorrelationMap = new ConcurrentHashMap<>();
        this.messageSource = messageSource;
    }

    // Register new User:
    @Override
    public Mono<UserDto> register(UserDto newUser) {
        return registerWithAuthenticationService(newUser.getUsername(), newUser.getPassword())
                .flatMap(ignored -> userRepository.save(newUser.mapToEntity())
                        .map(UserDto::new));
    }

    // Send username and password to AuthenticationService:
    private Mono<CredentialsDto> registerWithAuthenticationService(String username, String password) {
        return Mono.create(sink -> {
            // Generate an ID for this message:
            String correlationId = UUID.randomUUID().toString();

            // Use the ID to identify where to set the response once it comes in:
            registrationCorrelationMap.put(correlationId, sink);

            // Package the user credentials into an object to send to Authentication-Service:
            CredentialsDto credentials = new CredentialsDto(username, password);

            // Send the message with instructions for the response:
            rabbitTemplate.convertAndSend(Queues.REGISTRATION_REQUEST.getQueue(), credentials, message -> {
                message.getMessageProperties().setCorrelationId(correlationId);
                message.getMessageProperties().setReplyTo(Queues.REGISTRATION_RESPONSE.getQueue());
                return message;
            });
        });
    }

    // Receive response from AuthenticationService to verify success before proceeding:
    @RabbitListener(queues = "registration-response-queue")
    public Mono<Void> handleRegistrationResponse(@Payload CredentialsDto credentials, @Header(AmqpHeaders.CORRELATION_ID) String correlationId) {
        MonoSink<CredentialsDto> sink = registrationCorrelationMap.remove(correlationId);
        if(sink != null) {
            sink.success(credentials);
        }
        return Mono.empty();
    }

    // Find User by Username:
    @Override
    public Mono<UserDto> findById(String username) {
        return userRepository.findById(username.toLowerCase()).map(UserDto::new)
                .switchIfEmpty(Mono.error(new UserNotFoundException("{user.not.found}", username)));
    }

    // Find all Users:
    @Override
    public Flux<UserDto> findAll() {
        return userRepository.findAll().map(UserDto::new);
    }

    // Get a User's remaining reimbursement amount:
    @Override
    public Mono<BigDecimal> findAvailableBalanceByUsername(String username) {
        return findById(username)
                .map(UserDto::getRemainingBalance);
    }

    // Update User by Username:
    @Override
    public Mono<UserDto> updateUserByUsername(String username, UserDto updatedUser) {
        // First check if the user exists:
        return findById(username).flatMap(foundUser -> {
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
        return findById(username)
                .flatMap(foundUser -> userRepository.deleteById(username));
    }

    // Promote User to Department Head:
    @Override
    public Mono<UserDto> makeDepartmentHead(String username) {
        return findById(username.toLowerCase()).flatMap(user -> {
            user.setRole(Role.DEPARTMENT_HEAD);
            return userRepository.save(user.mapToEntity())
                    .map(UserDto::new);
        });
    }

    // Promote User to Benefits Coordinator:
    @Override
    public Mono<UserDto> makeBenco(String username) {
        return findById(username.toLowerCase())
                .flatMap(user -> {
                    user.setRole(Role.BENCO);
                    return userRepository.save(user.mapToEntity())
                            .map(UserDto::new);
                });
    }

    // Get User:
    @RabbitListener(queues = "user-lookup-queue")
    public Mono<Void> findUserByUsername(@Payload String username, @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                         @Header(AmqpHeaders.REPLY_TO) String replyTo) {
        return findById(username)
                .doOnSuccess(user -> {
                    ApproverDto foundUser = new ApproverDto(username, user.getRole().name());
                    rabbitTemplate.convertAndSend(replyTo, foundUser, message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        return message;
                    });
                }).then();
    }

    // Get Supervisor:
    @RabbitListener(queues = "supervisor-lookup-queue")
    public Mono<Void> findSupervisorByEmployeeUsername(@Payload String employeeUsername, @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                                    @Header(AmqpHeaders.REPLY_TO) String replyTo) {
        return findById(employeeUsername)
                .flatMap(user -> findById(user.getSupervisor()))
                .doOnNext(supervisor -> {
                    ApproverDto approver = new ApproverDto(supervisor.getUsername(), supervisor.getRole().name());
                    rabbitTemplate.convertAndSend(replyTo, approver, message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        return message;
                    });
                }).then();
    }

    // Get employee's Department Head:
    @RabbitListener(queues = "department-head-lookup-queue")
    public Mono<Void> findDepartmentHeadByEmployeeUsername(@Payload String employeeUsername, @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                                            @Header(AmqpHeaders.REPLY_TO) String replyTo) {

        return findById(employeeUsername)
                .map(UserDto::getDepartment)
                .map(departmentService::findByName)
                .flatMap(departmentDtoMono -> departmentDtoMono.map(DepartmentDto::getHead))
                .doOnNext(departmentHead -> {
                    ApproverDto approver = new ApproverDto(departmentHead, Role.DEPARTMENT_HEAD.name());
                    rabbitTemplate.convertAndSend(replyTo, approver, message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        return message;
                    });
                }).then();
    }

    // Get employee's Benefits Coordinator. Placeholder for now:
    @RabbitListener(queues = "benco-lookup-queue")
    public Mono<Void> findBencoByEmployeeUsername(@Payload String employeeUsername, @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                                           @Header(AmqpHeaders.REPLY_TO) String replyTo) {

        return findById(employeeUsername)
                .map(UserDto::getDepartment)
                .map(departmentService::findByName)
                .flatMap(departmentDtoMono -> departmentDtoMono.map(DepartmentDto::getHead))
                .doOnNext(departmentHead -> {
                    ApproverDto approver = new ApproverDto(departmentHead, Role.DEPARTMENT_HEAD.name());
                    rabbitTemplate.convertAndSend(replyTo, approver, message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        return message;
                    });
                }).then();
    }

    // Update User's balance due to approved Reimbursement Form:
    @RabbitListener(queues = "adjustment-request-queue")
    public Mono<Void> updateUserBalance(@Payload ReimbursementMessageDto reimbursementMessage, @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                         @Header(AmqpHeaders.REPLY_TO) String replyTo) {
        return findById(reimbursementMessage.getUsername())
                .flatMap(user -> {
                    // Reimburse the total amount listed on the Form unless it is greater than User's remaining balance, in which case award the remaining balance:
                    BigDecimal userBalance = user.getRemainingBalance();
                    BigDecimal grossReimbursement = reimbursementMessage.getReimbursement();
                    BigDecimal netReimbursement = userBalance.min(grossReimbursement);
                    reimbursementMessage.setReimbursement(netReimbursement);

                    // Update the User's remaining balance and then return the net reimbursement amount to Form-Service so the Form can be updated::
                    user.setRemainingBalance(userBalance.subtract(netReimbursement).setScale(2, RoundingMode.HALF_UP));
                    return userRepository.save(user.mapToEntity())
                            .thenReturn(reimbursementMessage);
                }).doOnNext(reimbursement -> rabbitTemplate.convertAndSend(replyTo, reimbursement, message -> {
                    message.getMessageProperties().setCorrelationId(correlationId);
                    return message;
                })).then();
    }

    // Restore Pending balance to User from cancelled request:
    @RabbitListener(queues = "cancel-request-queue")
    public Mono<Void> handleCancelRequest(@Payload ReimbursementMessageDto reimbursementMessage) {
        return findById(reimbursementMessage.getUsername())
                .flatMap(user -> {
                    user.setRemainingBalance(user.getRemainingBalance()
                            .add(reimbursementMessage.getReimbursement())
                            .setScale(2, RoundingMode.HALF_UP));
                    return userRepository.save(user.mapToEntity());
                }).then();
    }
}
