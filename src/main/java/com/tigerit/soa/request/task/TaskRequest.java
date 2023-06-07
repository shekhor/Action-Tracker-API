package com.tigerit.soa.request.task;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/*
Fahim created at 5/31/2020
*/
@Data
public class TaskRequest {

    private String id;

    @NotEmpty(message = "Task name can not by null or empty")
    private String taskName;

    @NotEmpty(message = "Project Id can not by null or empty")
    private String projectId;

    @NotEmpty(message = "Project name can not by null or empty")
    private String projectName;

    private String parentTaskId;
    private String departmentId;

    @NotEmpty(message = "Access Level can not by null or empty")
    private String accessLevel;

    private String item;
    private String action;

    @Min(value = 1, message = "Owner Id mandatory")
    private Long ownerId;
    @NotEmpty(message = "Owner name can not by null or empty")
    private String ownerName;

    private String status;

    private Date startDate;
    private Date dueDate;
    private Date endDate;

    private String contributor;
    private String priority;
    private String dependency;
    private String levelOfEffort;
    private String description;
    private String percentageCompleted;
    private String category;
    private String milestoneId;
}
