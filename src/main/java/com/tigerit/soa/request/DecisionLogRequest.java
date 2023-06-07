package com.tigerit.soa.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
Fahim created at 5/7/2020
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionLogRequest {

    private String id;
    private String decision;
    private String projectId;
    private String projectName;
}
