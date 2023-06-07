package com.tigerit.soa.request.teammember;

import com.tigerit.soa.model.es.TeamMember;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/*
Fahim created at 5/13/2020
*/
@Data
public class TeamMemberMappingRequest {
    TeamMember projectOwner;

    TeamMember projectManager;

    @NotEmpty(message = "Project name can not by null or empty")
    private String projectName;

    @NotEmpty(message = "Project Id can not by null or empty")
    private String projectId;

    List<TeamMember> teamMemberList;
}
