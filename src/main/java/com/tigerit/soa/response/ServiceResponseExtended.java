package com.tigerit.soa.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DIPU on 4/23/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseExtended<T> extends ServiceResponse<T> implements Serializable {
    private long pageNo;
    private long pageSize;
    private long totalHits;

    public ServiceResponseExtended(HttpStatus status, StatusCode statusCode, T body, long pageNo, long pageSize, long totalHits) {
        super();
        this.setStatus(status);
        this.setStatusCode(statusCode);
        this.setBody(body);
        this.pageNo = pageNo;
        this.pageSize=pageSize;
        this.totalHits=totalHits;
    }

    public ServiceResponseExtended buildServiceResponseExtended(ServiceResponse serviceResponse) {
        this.setErrorList(serviceResponse.getErrorList());
        this.setStatus(serviceResponse.getStatus());
        this.setStatusCode(serviceResponse.getStatusCode());
        this.setBody((T)serviceResponse.getBody());
        return this;
    }

    public ServiceResponseExtended buildSuccessServiceResponseExtended(T body, long pageNo, long pageSize,
                                                                       long totalHits) {
        this.setStatus(HttpStatus.OK);
        this.setStatusCode(StatusCode.SUCCESS);
        this.setBody(body);
        this.pageNo = pageNo;
        this.pageSize=pageSize;
        this.totalHits=totalHits;
        return this;
    }

    public ServiceResponseExtended buildFailedServiceResponseExtended(List<T> errorList) {
        this.setStatus(HttpStatus.BAD_REQUEST);
        this.setStatusCode(StatusCode.ERROR);
        this.setErrorList(errorList);
        return this;
    }
}
