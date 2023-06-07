package com.tigerit.soa.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/*
Fahim created at 5/5/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredRiskListRequest {

    @NotEmpty(message = "Project id can not by null or empty")
    private String projectId;
}
