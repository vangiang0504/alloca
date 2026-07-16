package com.company.alloca.exception;

public class AllocationNotFoundException extends RuntimeException {
    public AllocationNotFoundException(String message) {
        super(message);
    }
}
