package com.skillstorm.repositories;

import com.skillstorm.entities.User;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveCassandraRepository<User, String> {
}
