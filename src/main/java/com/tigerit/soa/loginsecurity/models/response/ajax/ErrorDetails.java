package com.tigerit.soa.loginsecurity.models.response.ajax;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetails {

    private String field;

    private String message;

    private String dataTab;

    public ErrorDetails() {
    }

    public ErrorDetails(String message) {
        this.message = message;
    }

    public ErrorDetails(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public ErrorDetails(String dataTab, String field, String message) {
        this.dataTab = dataTab;
        this.field = field;
        this.message = message;
    }
}
