package com.skillstorm.entities;

import com.skillstorm.constants.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;

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

    @Column("yearly_allowance")
    private BigDecimal yearlyAllowance;

    @Column("remaining_balance")
    private BigDecimal remainingBalance;

    private Role role;
}
