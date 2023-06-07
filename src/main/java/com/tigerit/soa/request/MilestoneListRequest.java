package com.tigerit.soa.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/*
Fahim created at 5/5/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneListRequest {

    @NotEmpty(message = "Project id can not by null or empty")
    private String projectId;

    @Min(value = 0, message = "Invalid page no")
    private int pageNo;

    @Min(value = 1, message = "Invalid item no per page")
    private int totalItemPerPage;
}
