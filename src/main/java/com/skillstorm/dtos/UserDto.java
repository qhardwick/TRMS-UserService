package com.skillstorm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skillstorm.entities.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    @NotNull(message = "{username.must}")
    @Size(min = 3, max = 25, message = "{username.size}")
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

    public UserDto(User user) {
        super();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
    }

    @JsonIgnore
    public User mapToUser() {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        return user;
    }
}
