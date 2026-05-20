package com.taller360.app.shared.exception;

public class InvalidStatusTransitionException extends BusinessRuleException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
