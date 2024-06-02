package com.skillstorm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skillstorm.entities.Department;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepartmentDto {

    @Size(min = 2, message = "{department.name.size}")
    private String name;

    @NotNull(message = "{department.head.must}")
    @Size(min = 3, max = 25, message = "{department.head.size")
    private String head;

    public DepartmentDto(Department department) {
        super();
        this.name = department.getName();
        this.head = department.getHead();
    }

    @JsonIgnore
    public Department mapToEntity() {
        Department department = new Department();
        department.setName(name.toUpperCase());
        department.setHead(head);

        return department;
    }
}
