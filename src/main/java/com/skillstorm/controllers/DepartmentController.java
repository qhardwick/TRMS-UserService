package com.skillstorm.controllers;

import com.skillstorm.dtos.DepartmentDto;
import com.skillstorm.services.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService =departmentService;
    }

    // Test endpoint:
    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello Departments");
    }

    // Create new Department:
    @PostMapping
    public Mono<ResponseEntity<DepartmentDto>> addDepartment(@Valid @RequestBody DepartmentDto newDepartment) {
        return departmentService.addDepartment(newDepartment)
                .map(createdDepartment -> ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment));
    }

    // Find all Departments:
    @GetMapping
    public Flux<DepartmentDto> findAll() {
        return departmentService.findAll();
    }

    // Find Department by name:
    @GetMapping("/{name}")
    public Mono<DepartmentDto> findByName(@PathVariable("name") String name) {
        return departmentService.findByName(name);
    }

    // Update Department Head:
    @PutMapping("/{name}")
    public Mono<DepartmentDto> updateDepartmentHead(@PathVariable("name") String name, @Valid @RequestBody DepartmentDto newHead) {
        return departmentService.updateDepartmentHead(name, newHead);
    }

    // Delete Department by name:
    @DeleteMapping("/{name}")
    public Mono<Void> deleteByName(@PathVariable("name") String name) {
        return departmentService.deleteByName(name);
    }
}
