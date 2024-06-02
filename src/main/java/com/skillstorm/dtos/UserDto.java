package com.skillstorm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skillstorm.constants.Role;
import com.skillstorm.entities.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    @NotNull(message = "{username.must}")
    @Size(min = 3, max = 25, message = "{username.size}")
    private String normalizedUsername;

    private String username;

    @NotNull(message = "{firstname.must}")
    @Size(min = 2, max = 50, message = "{firstname.size}")
    private String firstName;

    @NotNull(message = "{lastname.must}")
    @Size(min = 2, max = 50, message = "{lastname.size}")
    private String lastName;

    @NotNull(message = "{email.must}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "{email.invalid}")
    private String email;

    private String supervisor;

    private String department;

    private Role role;

    public UserDto() {
        this.role = Role.EMPLOYEE;
    }

    public UserDto(User user) {
        super();
        this.normalizedUsername = user.getNormalizedUsername();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.supervisor = user.getSupervisor();
        this.department = user.getDepartment();
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
        user.setRole(role);

        return user;
    }
}
