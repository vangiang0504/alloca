package com.company.alloca.entity;

/**
 * Enum representing the possible statuses of a resource allocation.
 * Workflow: PENDING → ACTIVE → ENDED
 */
public enum AllocationStatus {
    PENDING,
    ACTIVE,
    ENDED
}
