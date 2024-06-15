package com.skillstorm.services;

import com.skillstorm.dtos.DepartmentDto;
import com.skillstorm.dtos.UserDto;
import com.skillstorm.exceptions.UserNotFoundException;
import com.skillstorm.repositories.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DepartmentService departmentService;
    private final MessageSource messageSource;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, DepartmentService departmentService, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.departmentService = departmentService;
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
    @Override
    @RabbitListener(queues = "supervisor-lookup-queue")
    public Mono<String> findSupervisorByEmployeeUsername(String employeeUsername) {
        return findByUsername(employeeUsername)
                .map(UserDto::getSupervisor);
    }

    // Get Department Head:
    @Override
    @RabbitListener(queues = "department-head-lookup-queue")
    public Mono<String> findDepartmentHeadByEmployeeUsername(String employeeUsername) {
        return findByUsername(employeeUsername)
                .map(UserDto::getDepartment)
                .map(departmentService::findById)
                .flatMap(departmentDtoMono -> departmentDtoMono.map(DepartmentDto::getHead));
    }
}
