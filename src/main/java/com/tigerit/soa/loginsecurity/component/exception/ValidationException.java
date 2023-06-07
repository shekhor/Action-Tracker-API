package com.tigerit.soa.loginsecurity.component.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class ValidationException extends RuntimeException {
    private HttpStatus httpStatus;
    private String field;

    public ValidationException(HttpStatus httpStatus, String field) {
        this.httpStatus = httpStatus;
        this.field = field;
    }

    public ValidationException(HttpStatus httpStatus, String field, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.field = field;
    }

    public ValidationException(HttpStatus httpStatus, String field, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.field = field;
    }
}