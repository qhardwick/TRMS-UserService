package com.skillstorm.services;

import com.skillstorm.dtos.UserDto;
import com.skillstorm.exceptions.UserNotFoundException;
import com.skillstorm.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register new User:
    @Override
    public Mono<UserDto> register(UserDto newUser) {
        return userRepository.save(newUser.mapToUser()).map(UserDto::new);
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
        updatedUser.setUsername(username);
        return userRepository.save(updatedUser.mapToUser())
                .map(UserDto::new);
    }

    // Delete User by Username:
    @Override
    public Mono<Void> deleteByUsername(String username) {
        return userRepository.deleteById(username);
    }
}
