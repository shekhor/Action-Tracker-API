package com.tigerit.soa.model.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/*
Fahim created at 5/13/2020
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {
    private String id;
    @NotEmpty(message = "User id must be defined")
    private Long userId;
    private String firstName;
    private String lastName;
    private String domainName;
    @NotEmpty(message = "Organization id must be defined")
    private String organizationId;
    private String email;
    private String userRoleInProject;
    @NotEmpty(message = "Operation must be defined")
    private String operation;
}
