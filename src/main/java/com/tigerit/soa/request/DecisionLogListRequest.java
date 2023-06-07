package com.tigerit.soa.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/*
Fahim created at 5/7/2020
*/
@Data
public class DecisionLogListRequest {

    @NotEmpty(message = "Project id can not by null or empty")
    private String projectId;

    @Min(value = 0, message = "Invalid page no")
    private int pageNo;

    @Min(value = 1, message = "Invalid item no per page")
    private int totalItemPerPage;
}
