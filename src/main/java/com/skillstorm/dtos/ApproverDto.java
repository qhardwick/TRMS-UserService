package com.skillstorm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ApproverDto implements Serializable {

    private String username;
    private String role;
}
