package com.tigerit.soa.request.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/*
Fahim created at 5/17/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectListRequest {

    @Min(value = 0, message = "Invalid page no")
    private int pageNo;
    @Min(value = 1, message = "Invalid item no per page")
    private int totalItemPerPage;
}
