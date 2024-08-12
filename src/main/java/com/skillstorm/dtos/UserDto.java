package com.skillstorm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skillstorm.constants.Role;
import com.skillstorm.entities.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserDto {

    @NotNull(message = "{username.must}")
    @Size(min = 3, max = 25, message = "{username.size}")
    private String username;

    private String password;

    @NotNull(message = "{firstname.must}")
    @Size(min = 2, max = 50, message = "{firstname.size}")
    private String firstName;

    @NotNull(message = "{lastname.must}")
    @Size(min = 2, max = 50, message = "{lastname.size}")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "{email.invalid}")
    private String email;

    private String supervisor;

    private String department;

    private BigDecimal yearlyAllowance;

    private BigDecimal remainingBalance;

    private Role role;

    public UserDto() {
        this.role = Role.EMPLOYEE;
        this.yearlyAllowance = BigDecimal.valueOf(1000.00).setScale(2);
        this.remainingBalance = BigDecimal.valueOf(1000.00).setScale(2);
    }

    public UserDto(User user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.supervisor = user.getSupervisor();
        this.department = user.getDepartment();
        this.yearlyAllowance = user.getYearlyAllowance();
        this.remainingBalance = user.getRemainingBalance();
        this.role = user.getRole();
    }

    @JsonIgnore
    public User mapToEntity() {
        User user = new User();
        user.setNormalizedUsername(username.toLowerCase());
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setSupervisor(supervisor);
        user.setDepartment(department);
        user.setYearlyAllowance(yearlyAllowance);
        user.setRemainingBalance(remainingBalance);
        user.setRole(role);

        return user;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }
}
