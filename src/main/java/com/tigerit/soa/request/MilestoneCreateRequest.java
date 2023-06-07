package com.tigerit.soa.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneCreateRequest implements Serializable{

    @NotEmpty(message = "Project id can not by null or empty")
    private String projectId;

    @NotEmpty(message = "Project name can not by null or empty")
    private String projectName;

    @NotEmpty(message = "Milestone can not by null or empty")
    private String milestone;

    private String description;

    @NotEmpty(message = "Milestone date is mendatory")
    private Date milestoneDate;
}
