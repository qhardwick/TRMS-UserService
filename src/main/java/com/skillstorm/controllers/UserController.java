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

import java.math.BigDecimal;

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
    @PostMapping
    public Mono<ResponseEntity<UserDto>> register(@Valid @RequestBody Mono<UserDto> newUser) {
        return newUser.flatMap(userService::register)
                .map(createdUser -> ResponseEntity.status(HttpStatus.CREATED).body(createdUser));
    }

    // Find User by Username:
    @GetMapping("/{username}")
    public Mono<UserDto> findUserByUsername(@PathVariable("username") String username) {
        return userService.findById(username);
    }

    // View available reimbursement amount:
    // TODO: Limit this to either the relevant User and a BENCO:
    @GetMapping("/{username}/balance")
    public Mono<BigDecimal> getRemainingBalance(@PathVariable("username") String username) {
        return userService.findAvailableBalanceByUsername(username);
    }

    // Find all Users. Just for testing purposes:
    @GetMapping
    public Flux<UserDto> findAll() {
        return userService.findAll();
    }

    // Update User by Username:
    @PutMapping("/{username}")
    public Mono<UserDto> updateUserByUsername(@PathVariable("username") String username, @Valid @RequestBody Mono<UserDto> updatedUser) {
        return updatedUser.flatMap(userData -> userService.updateUserByUsername(username, userData));
    }

    // Promote to Department Head:
    // TODO: Benco (and/or maybe Department Head) only:
    // TODO: Maybe declare which Department and update the Department along with the User:
    @PutMapping("/{username}/department-head")
    public Mono<UserDto> makeDepartmentHead(@PathVariable("username") String username, @RequestHeader("username") String authorizer) {
        return userService.makeDepartmentHead(username);
    }

    // Promote to Benco:
    // TODO: Benco (and/or maybe Department Head) only:
    @PutMapping("/{username}/benco")
    public Mono<UserDto> makeBenco(@PathVariable("username") String username, @RequestHeader("username") String authorizer) {
        return userService.makeBenco(username);
    }

    // Delete User by Username:
    @DeleteMapping("/{username}")
    public Mono<Void> deleteUserByUsername(@PathVariable("username") String username) {
        return userService.deleteByUsername(username);
    }
}
