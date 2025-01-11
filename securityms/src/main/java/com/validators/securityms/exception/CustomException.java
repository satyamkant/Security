package com.validators.securityms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends Exception {
    Exception exception;
    public CustomException(String message, Exception exception) {
        super(message);
        this.exception = exception;
    }
}
