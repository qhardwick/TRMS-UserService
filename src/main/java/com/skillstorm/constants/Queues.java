package com.skillstorm.constants;

import lombok.Getter;

@Getter
public enum Queues {
    // From Form-Service:
    SUPERVISOR_LOOKUP("supervisor-lookup-queue"),
    DEPARTMENT_HEAD_LOOKUP("department-head-lookup-queue"),
    BENCO_LOOKUP("benco-lookup-queue"),
    ADJUSTMENT_REQUEST("adjustment-request-queue"),

    // To Form-Service:
    SUPERVISOR_RESPONSE("supervisor-response-queue"),
    DEPARTMENT_HEAD_RESPONSE("department-head-response-queue"),
    BENCO_RESPONSE("benco-response-queue"),
    ADJUSTMENT_RESPONSE("adjustment-response-queue");

    private final String queue;

    Queues(String queue) {
        this.queue = queue;
    }

    @Override
    public String toString() {
        return queue;
    }
}
