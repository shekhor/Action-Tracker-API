package com.tigerit.soa.request.teammember;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/*
Fahim created at 5/13/2020
*/
@Data
public class TeamMemberListRequest {
    @NotEmpty(message = "Project Id can not by null or empty")
    private String projectId;
}
