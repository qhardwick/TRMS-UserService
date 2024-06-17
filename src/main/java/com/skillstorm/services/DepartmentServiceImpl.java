package com.skillstorm.services;

import com.skillstorm.dtos.DepartmentDto;
import com.skillstorm.exceptions.DepartmentNotFoundException;
import com.skillstorm.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // Create new Department:
    @Override
    @CachePut(value = "departmentCache", key = "#name")
    public Mono<DepartmentDto> addDepartment(DepartmentDto newDepartment) {
        return departmentRepository.save(newDepartment.mapToEntity())
                .map(DepartmentDto::new);
    }

    // Find all Departments:
    @Override
    public Flux<DepartmentDto> findAll() {
        return departmentRepository.findAll()
                .map(DepartmentDto::new);
    }

    // Find Department by name:
    @Override
    @Cacheable(value = "departmentCache", key = "#name")
    public Mono<DepartmentDto> findByName(String name) {
        return departmentRepository.findById(name)
                .map(DepartmentDto::new)
                .switchIfEmpty(Mono.error(new DepartmentNotFoundException("department.not.found", name)));
    }

    // Update Department Head:
    @Override
    @CachePut(value = "departmentCache", key = "#name")
    public Mono<DepartmentDto> updateDepartmentHead(String name, DepartmentDto newHead) {
        newHead.setName(name);
        return departmentRepository.save(newHead.mapToEntity())
                .map(DepartmentDto::new);
    }

    // Delete Department by name:
    @Override
    @CacheEvict(value = "departmentCache", key = "#name")
    public Mono<Void> deleteByName(String name) {
        return departmentRepository.deleteById(name);
    }
}
