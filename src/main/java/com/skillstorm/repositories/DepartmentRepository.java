package com.skillstorm.repositories;

import com.skillstorm.entities.Department;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends ReactiveCassandraRepository<Department, String> {
}
