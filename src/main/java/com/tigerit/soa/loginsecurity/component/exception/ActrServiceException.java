package com.tigerit.soa.loginsecurity.component.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ActrServiceException extends RuntimeException {
    private HttpStatus httpStatus;
    private String field;

    public ActrServiceException(HttpStatus httpStatus, String field) {
        this.httpStatus = httpStatus;
        this.field = field;
    }

    public ActrServiceException(HttpStatus httpStatus, String field, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.field = field;
    }

    public ActrServiceException(HttpStatus httpStatus, String field, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.field = field;
    }
}

