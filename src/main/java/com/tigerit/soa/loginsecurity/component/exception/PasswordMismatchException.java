package com.tigerit.soa.loginsecurity.component.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordMismatchException extends RuntimeException {
    private String field;

    public PasswordMismatchException(String field, String message) {
        super(message);
        this.field = field;
    }
}