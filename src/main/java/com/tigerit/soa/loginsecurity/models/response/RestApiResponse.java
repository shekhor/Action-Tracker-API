package com.tigerit.soa.loginsecurity.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tigerit.soa.loginsecurity.models.response.ajax.*;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestApiResponse<T> implements Serializable {
    private String message;

    private HttpStatus status;

    private StatusCode statusCode;

    private SuccessDetails<T> success;

    private ErrorDetails error;

    private WarningDetails warning;

    private PendingResult pending;

    public RestApiResponse() {
    }

    public RestApiResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public RestApiResponse(HttpStatus status, ErrorDetails error) {
        this.statusCode = StatusCode.ERROR;
        this.status = status;
        this.error = error;
    }

    public RestApiResponse(HttpStatus status, PendingResult pending) {
        this.statusCode = StatusCode.PENDING;
        this.status = status;
        this.pending = pending;
    }

    public RestApiResponse(HttpStatus status, SuccessDetails<T> success) {
        this.statusCode = StatusCode.SUCCESS;
        this.status = status;
        this.success = success;
    }

    public RestApiResponse(HttpStatus status, WarningDetails warning) {
        this.statusCode = StatusCode.WARNING;
        this.status = status;
        this.warning = warning;
    }
}
