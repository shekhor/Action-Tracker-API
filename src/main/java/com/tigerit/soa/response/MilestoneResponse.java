package com.tigerit.soa.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneResponse implements Serializable {

    private String id;
    private String projectId;
    private String projectName;
    private String milestone;
    private String description;
    private Date milestoneDate;
    private boolean success;
}
