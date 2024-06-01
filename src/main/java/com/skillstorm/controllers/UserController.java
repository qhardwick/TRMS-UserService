package com.skillstorm.controllers;

import com.skillstorm.dtos.UserDto;
import com.skillstorm.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Test endpoint:
    @GetMapping("/hello")
    public Mono<String> hello(){
        return Mono.just("Hello UserService");
    }

    // Register new User:
    // We can still wrap the response in a ResponseEntity if we want to return a specific status like 201:
    @PostMapping
    public Mono<ResponseEntity<UserDto>> register(@Valid @RequestBody UserDto newUser) {
        return userService.register(newUser)
                .map(createdUser -> ResponseEntity.status(HttpStatus.CREATED).body(createdUser));
    }

    // Find User by Username:
    // SpringWebflux will automatically wrap it for us, though, so if we're fine with 200 we can just return the object:
    @GetMapping("/{username}")
    public Mono<UserDto> findUserByUsername(@PathVariable("username") String username) {
        return userService.findByUsername(username);
    }

    // Find all Users. Just for testing purposes:
    @GetMapping
    public Flux<UserDto> findAll() {
        return userService.findAll();
    }

    // Update User by Username:
    @PutMapping("/{username}")
    public Mono<UserDto> updateUserByUsername(@PathVariable("username") String username, @Valid @RequestBody UserDto updatedUser) {
        return userService.updateUserByUsername(username, updatedUser);
    }

    // Delete User by Username:
    @DeleteMapping("/{username}")
    public Mono<Void> deleteUserByUsername(@PathVariable("username") String username) {
        return userService.deleteByUsername(username);
    }
}
