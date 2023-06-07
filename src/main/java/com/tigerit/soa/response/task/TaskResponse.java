package com.tigerit.soa.response.task;

import lombok.Data;

import java.util.Date;

/*
Fahim created at 5/31/2020
*/
@Data
public class TaskResponse {

    private String id;
    private String taskName;
    private String projectId;
    private String projectName;
    private String parentTaskId;
    private String departmentId;
    private String accessLevel;
    private String item;
    private String action;
    private Long ownerId;
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
    private String milestoneDate;
    private String milestoneDescription;
}
