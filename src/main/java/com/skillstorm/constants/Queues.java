package com.skillstorm.constants;

import lombok.Getter;

@Getter
public enum Queues {

    // To Authentication-Service:
    REGISTRATION_REQUEST("registration-request-queue"),

    // From Authentication-Service:
    REGISTRATION_RESPONSE("registration-response-queue"),

    // From Form-Service:
    USER_LOOKUP("user-lookup-queue"),
    SUPERVISOR_LOOKUP("supervisor-lookup-queue"),
    DEPARTMENT_HEAD_LOOKUP("department-head-lookup-queue"),
    BENCO_LOOKUP("benco-lookup-queue"),
    ADJUSTMENT_REQUEST("adjustment-request-queue"),
    CANCEL_REQUEST("cancel-request-queue"),

    // To Form-Service:
    USER_REPONSE("user-response-queue"),
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
