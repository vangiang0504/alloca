package com.company.alloca.exception;

public class AllocationExceededException extends RuntimeException {
    public AllocationExceededException(String message) {
        super(message);
    }
}