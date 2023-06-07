package com.tigerit.soa.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

/*
Fahim created at 4/9/2020
*/
@Data
@NoArgsConstructor
public class ServiceResponse<T> implements Serializable {
    private HttpStatus status; //OK_Failure
    private StatusCode statusCode; //code
    private T body;
    private List<T> errorList;

    public ServiceResponse(HttpStatus status, StatusCode statusCode, T body, List<T> errorList) {
        this.setStatus(status);
        this.setStatusCode(statusCode);
        this.setBody(body);
        this.errorList = errorList;
    }
}
