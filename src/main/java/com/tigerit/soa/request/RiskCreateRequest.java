package com.tigerit.soa.request;

import lombok.Data;

import java.util.List;

/*
Fahim created at 5/5/2020
*/
@Data
public class RiskCreateRequest {

    private String projectId;
    List<RiskRegistrationRequest> registeredRiskList;
}
