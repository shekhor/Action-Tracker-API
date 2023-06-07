package com.tigerit.soa.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

/*
Fahim created at 4/12/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOrganizationRequest {
    @Min(value = 0, message = "Invalid page no")
    private int pageNo;
    @Min(value = 1, message = "Invalid item no per page")
    private int totalItemPerPage;
}
