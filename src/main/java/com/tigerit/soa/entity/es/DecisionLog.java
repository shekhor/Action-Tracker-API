package com.tigerit.soa.entity.es;

import lombok.Data;

/*
Fahim created at 5/7/2020
*/
@Data
public class DecisionLog {

    private String id;
    private String decision;
    private String projectId;
    private String projectName;
    private String status;
}
