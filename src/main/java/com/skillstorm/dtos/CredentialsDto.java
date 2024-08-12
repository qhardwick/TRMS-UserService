package com.skillstorm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CredentialsDto {

    private String username;
    private String password;
}
