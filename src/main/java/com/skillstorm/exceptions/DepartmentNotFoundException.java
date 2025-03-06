package com.skillstorm.exceptions;

public class DepartmentNotFoundException extends IllegalArgumentException {

    public DepartmentNotFoundException(String message) {
        super(message);
    }

    public DepartmentNotFoundException(String message, String name) {
        this(message + " " + name);
    }
}
