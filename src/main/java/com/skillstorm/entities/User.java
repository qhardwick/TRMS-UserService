package com.skillstorm.entities;

import com.skillstorm.constants.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("users")
public class User {

    @PrimaryKey("normalized_username")
    private String normalizedUsername;

    private String username;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private String email;

    private String supervisor;

    private String department;

    private Role role;
}
