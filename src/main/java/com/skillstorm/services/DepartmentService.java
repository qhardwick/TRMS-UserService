package com.skillstorm.services;

import com.skillstorm.dtos.DepartmentDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DepartmentService {

    // Create new Department:
    Mono<DepartmentDto> addDepartment(DepartmentDto newDepartment);

    // Find all Departments:
    Flux<DepartmentDto> findAll();

    // Find Department by name:
    Mono<DepartmentDto> findById(String name);

    // Update Department Head:
    Mono<DepartmentDto> updateDepartmentHead(String name, DepartmentDto newHead);

    // Delete Department by name:
    Mono<Void> deleteByName(String name);
}
