package com.tigerit.soa.request.task;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/*
Fahim created at 6/3/2020
*/
@Data
public class TaskSearchRequest {
    @NotEmpty(message = "Project Id can not by null or empty")
    private String projectId;

    @Min(value = 0, message = "Invalid page no")
    private int pageNo;

    @Min(value = 1, message = "Invalid item no per page")
    private int totalItemPerPage;

    private String id;
    private String taskName;
    private String projectName;

    private String parentTaskId;
    private String departmentId;
    private String accessLevel;

    private String item;
    private String action;
    private Long ownerId;
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
